package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    private final ValueSupplier typeSupplier;
    private final ValueSupplier correlIdSupplier;
    private final ValueSupplier ttlSupplier;
    private final ValueSupplier delaySupplier;
    private final List<ValueSupplier.IndexSupplier> propertySuppliers = new ArrayList<>();
    private final List<String> propertyNames = new ArrayList<>();
    private final List<Class<?>> propertyTypes = new ArrayList<>();
    private final Integer bodyIndex;
    private final BodyAs bodyAs;

    private ParsedMethodSupplier(final Method method, final PropertyResolver propertyResolver) {
        this.reflected = new ReflectedMethod(method);

        this.typeSupplier = reflected.resolveSupplier(OfType.class, OfType::value,
                () -> OneUtil.firstUpper(method.getName()));
        this.correlIdSupplier = reflected.resolveArgSupplier(OfCorrelationId.class, () -> UUID.randomUUID().toString());
        this.ttlSupplier = reflected.resolveSupplier(OfTtl.class, a -> propertyResolver.resolve(a.value()), null);
        this.delaySupplier = reflected.resolveSupplier(OfDelay.class, a -> propertyResolver.resolve(a.value()), null);

        reflected.allParametersWith(OfProperty.class, (parameter, index) -> {
            propertySuppliers.add(Integer.valueOf(index)::intValue);
            propertyNames.add(parameter.getAnnotation(OfProperty.class).value());
            propertyTypes.add(parameter.getType());
        });

        bodyIndex = reflected.firstPayloadParameter(PARAMETER_ANNOTATIONS);
        bodyAs = bodyIndex == null ? null : reflected.getParameter(bodyIndex)::getType;
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
        final var type = OneUtil.toString(getValue(typeSupplier, args, null));
        final var correlId = OneUtil.toString(getValue(correlIdSupplier, args, null));
        final var ttl = Optional.ofNullable(OneUtil.toString(getValue(ttlSupplier, args, config.ttl())))
                .filter(OneUtil::hasValue).map(Duration::parse).orElse(null);
        final var delay = Optional.ofNullable(OneUtil.toString(getValue(delaySupplier, args, config.ttl())))
                .filter(OneUtil::hasValue).map(Duration::parse).orElse(null);
        final var properties = new HashMap<String, Object>();

        for (var i = 0; i < propertySuppliers.size(); i++) {
            final var supplier = propertySuppliers.get(i);
            final var key = propertyNames.get(i);
            final var arg = args[supplier.get()];

            // Must have a property name for non-map values.
            if (!OneUtil.hasValue(key) && !propertyTypes.get(i).isAssignableFrom(Map.class)) {
                throw new IllegalArgumentException(
                        "Un-defined property name on parameter " + reflected.getParameter(supplier.get()));
            }

            if (propertyTypes.get(i).isAssignableFrom(Map.class)) {
                // Skip null maps.
                if (arg != null) {
                    properties.putAll(((Map<String, Object>) arg));
                }
            } else {
                properties.put(key, arg);
            }
        }

        final var body = bodyIndex == null ? null : args[bodyIndex];

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

    private static Object getValue(ValueSupplier supplier, Object[] args, Object def) {
        return supplier == null ? def
                : supplier instanceof IndexSupplier indexSupplier ? args[indexSupplier.get()]
                        : ((SimpleSupplier) supplier).get();
    }
}
