package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.MDC;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfGroupId;
import me.ehp246.aufjms.api.annotation.OfGroupSeq;
import me.ehp246.aufjms.api.annotation.OfMDC;
import me.ehp246.aufjms.api.annotation.OfMDC.Op;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.exception.JmsDispatchException;
import me.ehp246.aufjms.api.jms.BodyOf;
import me.ehp246.aufjms.api.jms.FromJson;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.ExpressionResolver;
import me.ehp246.aufjms.core.dispatch.DefaultProxyInvocationBinder.PropertyArg;
import me.ehp246.aufjms.core.reflection.ReflectedMethod;
import me.ehp246.aufjms.core.reflection.ReflectedParameter;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultDispatchMethodParser implements DispatchMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set
            .of(OfType.class, OfProperty.class, OfTtl.class, OfDelay.class, OfCorrelationId.class);

    private final ExpressionResolver expressionResolver;
    private final FromJson fromJson;

    DefaultDispatchMethodParser(final ExpressionResolver expressionResolver, final FromJson fromJson) {
        this.expressionResolver = expressionResolver;
        this.fromJson = fromJson;
    }

    @Override
    public DispatchMethodBinder parse(final Method method, final ByJmsProxyConfig config) {
        final var reflected = new ReflectedMethod(method);

        return new DispatchMethodBinder(parseInvocationBinder(reflected, config),
                parseReturnBinder(reflected, config));
    }

    private InvocationDispatchBinder parseInvocationBinder(final ReflectedMethod reflected,
            final ByJmsProxyConfig config) {
        final var typeFn = reflected.allParametersWith(OfType.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> (String) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfType.class)
                        .map(an -> (Function<Object[], String>) args -> an.value())
                        .orElseGet(() -> args -> OneUtil.firstUpper(reflected.method().getName())));

        final var correlIdFn = reflected.allParametersWith(OfCorrelationId.class).stream()
                .findFirst().map(p -> {
                    final var index = p.index();
                    return (Function<Object[], String>) args -> (String) args[index];
                }).orElse(null);

        final var ttlFn = reflected.allParametersWith(OfTtl.class).stream().findFirst()
                .map(p -> (Function<Object[], Duration>) args -> (Duration) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfTtl.class)
                        .map(a -> (Function<Object[], Duration>) args -> Duration
                                .parse(expressionResolver.resolve(a.value())))
                        .orElse(null));

        final var delayFn = reflected.allParametersWith(OfDelay.class).stream().findFirst()
                .map(p -> {
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
                    throw new IllegalArgumentException("Un-supported Delay type '" + type.getName()
                            + "' on '" + reflected.method().toString() + "'");
                }).orElseGet(() -> reflected.findOnMethodUp(OfDelay.class).map(a -> {
                    final var parsed = Duration.parse(expressionResolver.resolve(a.value()));
                    return (Function<Object[], Duration>) args -> parsed;
                }).orElse(null));

        final var groupIdFn = reflected.allParametersWith(OfGroupId.class).stream().findFirst()
                .map(p -> {
                    final var type = p.parameter().getType();
                    if (type.isAssignableFrom(String.class)) {
                        return (Function<Object[], String>) args -> (String) args[p.index()];
                    }
                    throw new IllegalArgumentException("Un-supported GroupId type '"
                            + type.getName() + "' on '" + reflected.method().toString() + "'");
                }).orElseGet(() -> reflected.findOnMethodUp(OfGroupId.class).map(a -> {
                    final var parsed = expressionResolver.resolve(a.value());
                    return (Function<Object[], String>) args -> parsed;
                }).orElse(null));

        final var groupSeqFn = reflected.allParametersWith(OfGroupSeq.class).stream().findFirst()
                .map(p -> {
                    final var type = p.parameter().getType();
                    if (type == int.class || type.isAssignableFrom(Integer.class)) {
                        return (Function<Object[], Integer>) args -> (Integer) args[p.index()];
                    }
                    throw new IllegalArgumentException("Un-supported GroupSeq type '"
                            + type.getName() + "' on '" + reflected.method().toString() + "'");
                }).orElse(null);

        final var bodyParamIndex = reflected.firstPayloadParameter(PARAMETER_ANNOTATIONS)
                .map(ReflectedParameter::index).orElse(-1);

        final var bodyOf = Optional
                .ofNullable(bodyParamIndex == -1 ? null : reflected.getParameter(bodyParamIndex))
                .map(parameter -> new BodyOf<>(Optional
                        .ofNullable(parameter.getAnnotation(JsonView.class)).map(JsonView::value)
                        .filter(OneUtil::hasValue).map(views -> views[0]).orElse(null),
                        parameter.getType()))
                .orElse(null);

        final Map<String, Function<Object[], String>> msgMDCBinders = new HashMap<String, Function<Object[], String>>();

        msgMDCBinders.putAll(reflected.allParametersWith(OfMDC.class).stream()
                .filter(p -> p.parameter().getAnnotation(OfMDC.class).op() == Op.Default)
                .collect(Collectors.toMap(p -> {
                    final var name = p.parameter().getAnnotation(OfMDC.class).value();
                    return OneUtil.hasValue(name) ? name : p.parameter().getName();
                }, p -> {
                    final var index = p.index();
                    return (Function<Object[], String>) (args -> args[index] == null ? null
                            : args[index] + "");
                }, (l, r) -> r)));
        /*
         * There is an annotated body parameter.
         */
        if (bodyParamIndex >= 0 && reflected.getParameter(bodyParamIndex)
                .getAnnotation(OfMDC.class) != null) {
            final var bodyParam = reflected.getParameter(bodyParamIndex);
            final var ofMDC = bodyParam.getAnnotation(OfMDC.class);

            switch (ofMDC.op()) {
                case Introspect:
                    /*
                     * Duplicated names will overwrite each other un-deterministically.
                     */
                    final var bodyParamContextName = ofMDC.value();

                    final var bodyFieldBinders = new ReflectedType<>(bodyParam.getType())
                            .streamSuppliersWith(OfMDC.class)
                            .filter(p -> p.getAnnotation(OfMDC.class)
                                    .op() == Op.Default)
                            .collect(
                                    Collectors.toMap(
                                            m -> bodyParamContextName + Optional
                                                    .of(m.getAnnotation(OfMDC.class)
                                                            .value())
                                                    .filter(OneUtil::hasValue)
                                                    .orElseGet(() -> m.getName()),
                                            Function.identity(), (l, r) -> r))
                            .entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
                                final var m = entry.getValue();
                                return (Function<Object[], String>) args -> {
                                    final var body = args[bodyParamIndex];
                                    if (body == null) {
                                        return null;
                                    }
                                    try {
                                        final var ret = m.invoke(body);
                                        return ret == null ? null : ret + "";
                                    } catch (IllegalAccessException | IllegalArgumentException
                                            | InvocationTargetException e) {
                                        throw new RuntimeException(e);
                                    }
                                };
                            }));
                    msgMDCBinders.putAll(bodyFieldBinders);
                    break;
                default:
                    msgMDCBinders.put(
                            Optional.ofNullable(bodyParam.getAnnotation(OfMDC.class))
                                    .map(OfMDC::value).filter(OneUtil::hasValue)
                                    .orElseGet(bodyParam::getName),
                            args -> (args[bodyParamIndex] == null ? null
                                    : args[bodyParamIndex] + ""));
                    break;
            }
        }

        return new DefaultProxyInvocationBinder(reflected, config, typeFn, correlIdFn,
                bodyParamIndex, bodyOf, propArgs(reflected), propStatic(reflected, config), ttlFn,
                delayFn, groupIdFn, groupSeqFn,
                msgMDCBinders.isEmpty() ? null : msgMDCBinders);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private InvocationReturnBinder parseReturnBinder(final ReflectedMethod reflected,
            final ByJmsProxyConfig config) {
        if (reflected.returnsVoid()) {
            return (LocalReturnBinder) dispatch -> null;
        }

        final var bodyOf = new BodyOf(reflected.method().getReturnType());
        final var requestTimeout = config.requestTimeout();

        return (RemoteReturnBinder) (jmsDispatch, replyFuture) -> {
            Optional.ofNullable(jmsDispatch.mdc()).orElseGet(Map::of).entrySet().stream()
                    .forEach(e -> MDC.put(e.getKey(), e.getValue()));
            try {
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
            } finally {
                Optional.ofNullable(jmsDispatch.mdc()).orElseGet(Map::of).entrySet()
                        .stream().forEach(e -> MDC.remove(e.getKey()));
            }
        };
    }

    private Map<String, String> propStatic(final ReflectedMethod reflected,
            final ByJmsProxyConfig config) {
        final var properties = config.properties();
        if ((properties.size() & 1) != 0) {
            throw new IllegalArgumentException("Properties should be in name/value pairs on "
                    + reflected.method().getDeclaringClass());
        }

        final Map<String, String> propStatic = new HashMap<>();
        for (int i = 0; i < properties.size(); i += 2) {
            final var key = properties.get(i);
            if (propStatic.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate '" + properties.get(i) + " on "
                        + reflected.method().getDeclaringClass());
            }
            propStatic.put(key, expressionResolver.resolve(properties.get(i + 1)));
        }
        return propStatic;
    }

    private Map<Integer, PropertyArg> propArgs(final ReflectedMethod reflected) {
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
