package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfGroupId;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatch.BodyAs;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.reflection.ReflectedParameter;
import me.ehp246.aufjms.core.reflection.ReflectedProxyMethod;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfType.class, OfProperty.class,
            OfTtl.class, OfDelay.class, OfCorrelationId.class);
    private final PropertyResolver propertyResolver;

    ProxyMethodParser(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    ParsedDispatchMethod parse(final Method method, final ByJmsProxyConfig config) {
        final var reflected = new ReflectedProxyMethod(method);

        final var typeFn = reflected.allParametersWith(OfType.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> (String) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfType.class)
                        .map(an -> (Function<Object[], String>) args -> an.value())
                        .orElseGet(() -> args -> OneUtil.firstUpper(method.getName())));

        final var correlIdFn = reflected.allParametersWith(OfCorrelationId.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> (String) args[p.index()]).orElse(null);

        final var ttlFn = reflected.allParametersWith(OfTtl.class).stream().findFirst()
                .map(p -> (Function<Object[], Duration>) args -> (Duration) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfTtl.class).map(
                        a -> (Function<Object[], Duration>) args -> Duration.parse(propertyResolver.resolve(a.value())))
                        .orElse(null));

        final var delayFn = reflected.allParametersWith(OfDelay.class).stream().findFirst().map(p -> {
            final var type = p.parameter().getType();
            if (type.isAssignableFrom(String.class)) {
                return (Function<Object[], Duration>) args -> Duration.parse((String) args[p.index()]);
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

        final var propArgs = new ArrayList<Integer>();
        final var propNames = new ArrayList<String>();
        final var propTypes = new ArrayList<Class<?>>();
        reflected.allParametersWith(OfProperty.class).stream().forEach(p -> {
            propArgs.add(p.index());
            propNames.add(p.parameter().getAnnotation(OfProperty.class).value());
            propTypes.add(p.parameter().getType());
        });

        final var propertyArgs = new int[propArgs.size()];
        final var propertyNames = new String[propArgs.size()];
        final var propertyTypes = new Class[propArgs.size()];
        for (int i = 0; i < propArgs.size(); i++) {
            propertyArgs[i] = propArgs.get(i);
            propertyNames[i] = propNames.get(i);
            propertyTypes[i] = propTypes.get(i);
        }

        final var bodyIndex = reflected.firstPayloadParameter(PARAMETER_ANNOTATIONS).map(ReflectedParameter::index)
                .orElse(-1);
        final BodyAs bodyAs = bodyIndex == -1 ? null : reflected.getParameter(bodyIndex)::getType;

        return new ParsedDispatchMethod(reflected, config, typeFn, correlIdFn, bodyIndex, bodyAs, propertyArgs,
                propertyTypes, propertyNames, ttlFn, delayFn, groupIdFn, null);
    }
}
