package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfGroupId;
import me.ehp246.aufjms.api.annotation.OfGroupSeq;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.exception.JmsDispatchException;
import me.ehp246.aufjms.api.jms.BodyOf;
import me.ehp246.aufjms.api.jms.FromJson;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.dispatch.DefaultProxyInvocationBinder.PropertyArg;
import me.ehp246.aufjms.core.reflection.ReflectedParameter;
import me.ehp246.aufjms.core.reflection.ReflectedProxyMethod;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultDispatchMethodParser implements DispatchMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfType.class, OfProperty.class,
            OfTtl.class, OfDelay.class, OfCorrelationId.class);

    private final PropertyResolver propertyResolver;
    private final FromJson fromJson;

    DefaultDispatchMethodParser(final PropertyResolver propertyResolver, final FromJson fromJson) {
        this.propertyResolver = propertyResolver;
        this.fromJson = fromJson;
    }

    @Override
    public DispatchMethodBinder parse(final Method method, final ByJmsProxyConfig config) {
        final var reflected = new ReflectedProxyMethod(method);

        return new DispatchMethodBinder(parseInvocationBinder(reflected, config), parseReturnBinder(reflected, config));
    }

    private InvocationDispatchBinder parseInvocationBinder(final ReflectedProxyMethod reflected,
            final ByJmsProxyConfig config) {
        final var typeFn = reflected.allParametersWith(OfType.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> (String) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfType.class)
                        .map(an -> (Function<Object[], String>) args -> an.value())
                        .orElseGet(() -> args -> OneUtil.firstUpper(reflected.method().getName())));

        final var correlIdFn = reflected.allParametersWith(OfCorrelationId.class).stream().findFirst().map(p -> {
            final var index = p.index();
            return (Function<Object[], String>) args -> (String) args[index];
        }).orElse(null);

        final var ttlFn = reflected.allParametersWith(OfTtl.class).stream().findFirst()
                .map(p -> (Function<Object[], Duration>) args -> (Duration) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfTtl.class).map(
                        a -> (Function<Object[], Duration>) args -> Duration.parse(propertyResolver.resolve(a.value())))
                        .orElse(null));

        final var delayFn = reflected.allParametersWith(OfDelay.class).stream().findFirst().map(p -> {
            final var type = p.parameter().getType();
            if (type.isAssignableFrom(String.class)) {
                return (Function<Object[], Duration>) args -> {
                    final var delayArg = args[p.index()];
                    if (delayArg == null) {
                        return null;
                    }
                    return Duration.parse((String) delayArg);
                };
            } else if (type.isAssignableFrom(Duration.class)) {
                return (Function<Object[], Duration>) args -> (Duration) args[p.index()];
            }
            throw new IllegalArgumentException(
                    "Un-supported Delay type '" + type.getName() + "' on '" + reflected.method().toString() + "'");
        }).orElseGet(() -> reflected.findOnMethodUp(OfDelay.class).map(a -> {
            final var parsed = Duration.parse(propertyResolver.resolve(a.value()));
            return (Function<Object[], Duration>) args -> parsed;
        }).orElse(null));

        final var groupIdFn = reflected.allParametersWith(OfGroupId.class).stream().findFirst().map(p -> {
            final var type = p.parameter().getType();
            if (type.isAssignableFrom(String.class)) {
                return (Function<Object[], String>) args -> (String) args[p.index()];
            }
            throw new IllegalArgumentException(
                    "Un-supported GroupId type '" + type.getName() + "' on '" + reflected.method().toString() + "'");
        }).orElseGet(() -> reflected.findOnMethodUp(OfGroupId.class).map(a -> {
            final var parsed = propertyResolver.resolve(a.value());
            return (Function<Object[], String>) args -> parsed;
        }).orElse(null));

        final var groupSeqFn = reflected.allParametersWith(OfGroupSeq.class).stream().findFirst().map(p -> {
            final var type = p.parameter().getType();
            if (type == int.class || type.isAssignableFrom(Integer.class)) {
                return (Function<Object[], Integer>) args -> (Integer) args[p.index()];
            }
            throw new IllegalArgumentException(
                    "Un-supported GroupSeq type '" + type.getName() + "' on '" + reflected.method().toString() + "'");
        }).orElse(null);

        final var bodyIndex = reflected.firstPayloadParameter(PARAMETER_ANNOTATIONS).map(ReflectedParameter::index)
                .orElse(-1);

        final var bodyOf = Optional.ofNullable(bodyIndex == -1 ? null : reflected.getParameter(bodyIndex))
                .map(parameter -> new BodyOf<>(Optional.ofNullable(parameter.getAnnotation(JsonView.class))
                        .map(JsonView::value).filter(OneUtil::hasValue).map(views -> views[0]).orElse(null),
                        parameter.getType()))
                .orElse(null);

        return new DefaultProxyInvocationBinder(reflected, config, typeFn, correlIdFn, bodyIndex, bodyOf,
                propArgs(reflected), propStatic(reflected, config), ttlFn, delayFn, groupIdFn, groupSeqFn);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private InvocationReturnBinder parseReturnBinder(final ReflectedProxyMethod reflected,
            final ByJmsProxyConfig config) {
        if (reflected.returnsVoid()) {
            return (LocalReturnBinder) dispatch -> null;
        }

        final var bodyOf = new BodyOf(reflected.method().getReturnType());
        final var requestTimeout = config.requestTimeout();

        return (RemoteReturnBinder) (jmsDispatch, replyFuture) -> {
            final JmsMsg msg;
            try {
                msg = requestTimeout == null ? replyFuture.get()
                        : replyFuture.get(requestTimeout.toSeconds(), TimeUnit.SECONDS);
            } catch (Exception e) {
                if (reflected.isOnThrows(e.getClass())) {
                    throw e;
                }
                throw new JmsDispatchException(e);
            }

            return fromJson.apply(msg.text(), bodyOf);
        };
    }

    private Map<String, String> propStatic(final ReflectedProxyMethod reflected, final ByJmsProxyConfig config) {
        final var properties = config.properties();
        if ((properties.size() & 1) != 0) {
            throw new IllegalArgumentException(
                    "Properties should be in name/value pairs on " + reflected.method().getDeclaringClass());
        }

        final Map<String, String> propStatic = new HashMap<>();
        for (int i = 0; i < properties.size(); i += 2) {
            final var key = properties.get(i);
            if (propStatic.containsKey(key)) {
                throw new IllegalArgumentException(
                        "Duplicate '" + properties.get(i) + " on " + reflected.method().getDeclaringClass());
            }
            propStatic.put(key, propertyResolver.resolve(properties.get(i + 1)));
        }
        return propStatic;
    }

    private Map<Integer, PropertyArg> propArgs(final ReflectedProxyMethod reflected) {
        final Map<Integer, PropertyArg> propArgs = new HashMap<Integer, PropertyArg>();
        for (final var p : reflected.allParametersWith(OfProperty.class)) {
            final var parameter = p.parameter();
            propArgs.put(p.index(),
                    new DefaultProxyInvocationBinder.PropertyArg(
                            OneUtil.getIfBlank(parameter.getAnnotation(OfProperty.class).value(),
                                    () -> OneUtil.firstUpper(parameter.getName())),
                            parameter.getType()));
        }
        return propArgs;
    }
}
