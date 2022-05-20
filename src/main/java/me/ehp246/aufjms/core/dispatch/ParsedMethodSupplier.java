package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.core.reflection.ReflectedMethod;
import me.ehp246.aufjms.core.reflection.ValueSupplier;
import me.ehp246.aufjms.core.reflection.ValueSupplier.IndexSupplier;
import me.ehp246.aufjms.core.reflection.ValueSupplier.StaticSupplier;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
record ParsedMethodSupplier(ValueSupplier typeSupplier, ValueSupplier correlIdSupplier) {
    private static final Map<Method, ParsedMethodSupplier> CACHED = new ConcurrentHashMap<>();

    private static ParsedMethodSupplier parse(final Method method) {
        final var reflected = new ReflectedMethod(method);

        // Type
        return new ParsedMethodSupplier(reflected.resolveSupplier(OfType.class, OfType::value, () -> OneUtil.firstUpper(method.getName())), 
                reflected.resolveArgSupplier(OfCorrelationId.class, () -> UUID.randomUUID().toString()));
    }

    public static ParsedMethodSupplier get(final Method method) {
        return CACHED.computeIfAbsent(method, m -> ParsedMethodSupplier.parse(method));
    }

    public JmsDispatch apply(final ByJmsProxyHandler handler, final Object[] args) {
        final var to = handler.to();
        final var type = OneUtil
                .toString(typeSupplier instanceof IndexSupplier indexSupplier ? args[indexSupplier.get()]
                        : ((StaticSupplier) typeSupplier).get());
        final var correlId = OneUtil.toString(correlIdSupplier instanceof IndexSupplier indexSupplier ? args[indexSupplier.get()]
                : ((StaticSupplier) correlIdSupplier).get());

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
                return handler.replyTo();
            }

            @Override
            public Duration ttl() {
                // TODO Auto-generated method stub
                return JmsDispatch.super.ttl();
            }

            @Override
            public Map<String, Object> properties() {
                // TODO Auto-generated method stub
                return JmsDispatch.super.properties();
            }

            @Override
            public Duration delay() {
                // TODO Auto-generated method stub
                return JmsDispatch.super.delay();
            }

        };
    }
}
