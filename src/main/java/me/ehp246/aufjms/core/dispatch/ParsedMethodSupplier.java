package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatch.BodyAs;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.reflection.ReflectedMethod;
import me.ehp246.aufjms.core.reflection.ValueSupplier;
import me.ehp246.aufjms.core.reflection.ValueSupplier.IndexSupplier;
import me.ehp246.aufjms.core.reflection.ValueSupplier.SimpleSupplier;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class ParsedMethodSupplier {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfType.class, OfProperty.class,
            OfTtl.class, OfDelay.class, OfCorrelationId.class);

    private final ReflectedMethod reflected;
    private final Function<Object[], String> typeFn;
    private final Function<Object[], String> correlIdFn;
    private final Function<Object[], Duration> ttlFn;
    private final Function<Object[], Duration> delayFn;
    private final int[] propertyArgs;
    private final String[] propertyNames;
    private final Class<?>[] propertyTypes;
    private final int bodyIndex;
    private final BodyAs bodyAs;

    private ParsedMethodSupplier(final Method method, final PropertyResolver propertyResolver) {
        this.reflected = new ReflectedMethod(method);

        this.typeFn = reflected.allParametersWith(OfType.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> (String) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfType.class)
                        .map(an -> (Function<Object[], String>) args -> an.value())
                        .orElseGet(() -> args -> OneUtil.firstUpper(method.getName())));

        this.correlIdFn = reflected.allParametersWith(OfCorrelationId.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> (String) args[p.index()]).orElse(null);

        this.ttlFn = reflected.allParametersWith(OfTtl.class).stream().findFirst()
                .map(p -> (Function<Object[], Duration>) args -> (Duration) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfTtl.class).map(
                        a -> (Function<Object[], Duration>) args -> Duration.parse(propertyResolver.resolve(a.value())))
                        .orElse(null));

        this.delayFn = reflected.allParametersWith(OfDelay.class).stream().findFirst()
                .map(p -> (Function<Object[], Duration>) args -> (Duration) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(OfDelay.class).map(
                        a -> (Function<Object[], Duration>) args -> Duration.parse(propertyResolver.resolve(a.value())))
                        .orElse(null));

        final var propArgs = new ArrayList<Integer>();
        final var propNames = new ArrayList<String>();
        final var propTypes = new ArrayList<Class<?>>();
        reflected.allParametersWith(OfProperty.class, (parameter, index) -> {
            propArgs.add(index);
            propNames.add(parameter.getAnnotation(OfProperty.class).value());
            propTypes.add(parameter.getType());
        });

        this.propertyArgs = new int[propArgs.size()];
        this.propertyNames = new String[propArgs.size()];
        this.propertyTypes = new Class[propArgs.size()];
        for (int i = 0; i < propArgs.size(); i++) {
            this.propertyArgs[i] = propArgs.get(i);
            this.propertyNames[i] = propNames.get(i);
            this.propertyTypes[i] = propTypes.get(i);
        }

        bodyIndex = Optional.ofNullable(reflected.firstPayloadParameter(PARAMETER_ANNOTATIONS)).orElse(-1);
        bodyAs = bodyIndex == -1 ? null : reflected.getParameter(bodyIndex)::getType;
    }

    public static ParsedMethodSupplier parse(final Method method) {
        return parse(method, Object::toString);
    }

    public static ParsedMethodSupplier parse(final Method method, final PropertyResolver propertyResolver) {
        return new ParsedMethodSupplier(method, propertyResolver);
    }

    @SuppressWarnings("unchecked")
    public JmsDispatch apply(final ByJmsProxyConfig config, final Object[] args) {
        final var to = config.to();

        final String type = this.typeFn.apply(args);
        final String correlId = this.correlIdFn == null ? UUID.randomUUID().toString() : this.correlIdFn.apply(args);

        final Duration ttl = this.ttlFn == null ? config.ttl() : this.ttlFn.apply(args);
        final Duration delay = this.delayFn == null ? config.delay() : this.delayFn.apply(args);

        final var properties = new HashMap<String, Object>();

        for (var i = 0; i < propertyArgs.length; i++) {
            final var argIndex = propertyArgs[i];
            final var key = propertyNames[i];
            final var arg = args[argIndex];

            // Must have a property name for non-map values.
            if (!OneUtil.hasValue(key) && !propertyTypes[i].isAssignableFrom(Map.class)) {
                throw new IllegalArgumentException(
                        "Un-defined property name on parameter " + reflected.getParameter(argIndex));
            }

            if (propertyTypes[i].isAssignableFrom(Map.class)) {
                // Skip null maps.
                if (arg != null) {
                    properties.putAll(((Map<String, Object>) arg));
                }
            } else {
                properties.put(key, arg);
            }
        }

        final var body = bodyIndex == -1 ? null : args[bodyIndex];

        return new JmsDispatch() {

            @Override
            public At to() {
                return to;
            }

            @Override
            public String type() {
                return type;
            }

            @Override
            public String correlationId() {
                return correlId;
            }

            @Override
            public Object body() {
                return body;
            }

            @Override
            public BodyAs bodyAs() {
                return bodyAs;
            }

            @Override
            public At replyTo() {
                return config.replyTo();
            }

            @Override
            public Duration ttl() {
                return ttl;
            }

            @Override
            public Map<String, Object> properties() {
                return properties;
            }

            @Override
            public Duration delay() {
                return delay;
            }

        };
    }

    private static Duration applyDurationArgs(final ValueSupplier supplier, Object[] args, final Duration defValue) {
        return supplier == null ? defValue
                : Optional.ofNullable(applyStringArgs(supplier, args)).map(Duration::parse).orElse(null);
    }

    private static String applyStringArgs(ValueSupplier supplier, Object[] args) {
        return supplier instanceof IndexSupplier indexSupplier
                ? Optional.ofNullable(args[indexSupplier.get()]).map(Object::toString).orElse(null)
                : ((SimpleSupplier) supplier).get();
    }
}
