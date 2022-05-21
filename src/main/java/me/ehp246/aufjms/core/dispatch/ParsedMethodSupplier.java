package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
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
record ParsedMethodSupplier(ValueSupplier typeSupplier, ValueSupplier correlIdSupplier, ValueSupplier ttlSupplier,
        ValueSupplier delaySupplier) {
    public static ParsedMethodSupplier parse(final Method method) {
        return parse(method, Object::toString);
    }

    public static ParsedMethodSupplier parse(final Method method, final PropertyResolver propertyResolver) {
        final var reflected = new ReflectedMethod(method);

        // Type
        return new ParsedMethodSupplier(reflected.resolveSupplier(OfType.class, OfType::value, () -> OneUtil.firstUpper(method.getName())), 
                reflected.resolveArgSupplier(OfCorrelationId.class, () -> UUID.randomUUID().toString()),
                reflected.resolveSupplier(OfTtl.class, a -> propertyResolver.resolve(a.value()), null),
                reflected.resolveSupplier(OfDelay.class, a -> propertyResolver.resolve(a.value()), null));
    }

    public JmsDispatch apply(final ByJmsProxyConfig config, final Object[] args) {
        final var to = config.to();
        final var type = OneUtil.toString(getValue(typeSupplier, args, null));
        final var correlId = OneUtil.toString(getValue(correlIdSupplier, args, null));
        final var ttl = Optional.ofNullable(OneUtil.toString(getValue(ttlSupplier, args, config.ttl())))
                .filter(OneUtil::hasValue)
                .map(Duration::parse).orElse(null);
        final var delay = Optional.ofNullable(OneUtil.toString(getValue(delaySupplier, args, config.ttl())))
                .filter(OneUtil::hasValue).map(Duration::parse).orElse(null);

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
                // TODO Auto-generated method stub
                return JmsDispatch.super.body();
            }

            @Override
            public BodyAs bodyAs() {
                // TODO Auto-generated method stub
                return JmsDispatch.super.bodyAs();
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
                return JmsDispatch.super.properties();
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
