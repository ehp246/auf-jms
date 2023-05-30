package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.BodyOf;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.core.reflection.ReflectedProxyMethod;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class DefaultProxyInvocationBinder implements InvocationDispatchBinder {
    private final ReflectedProxyMethod reflected;
    private final ByJmsProxyConfig config;
    private final Function<Object[], String> typeFn;
    private final Function<Object[], String> correlIdFn;
    private final Function<Object[], String> groupIdFn;
    private final Function<Object[], Integer> groupSeqFn;
    private final Function<Object[], Duration> ttlFn;
    private final Function<Object[], Duration> delayFn;
    private final Map<Integer, PropertyArg> propArgs;
    private final Map<String, String> propStatic;
    private final int bodyIndex;
    private final BodyOf<?> bodyOf;

    DefaultProxyInvocationBinder(final ReflectedProxyMethod reflected, final ByJmsProxyConfig config,
            final Function<Object[], String> typeFn, final Function<Object[], String> correlIdFn, final int bodyIndex,
            final BodyOf<?> bodyAs, final Map<Integer, PropertyArg> propArgs,
            final Map<String, String> propStatic, final Function<Object[], Duration> ttlFn,
            final Function<Object[], Duration> delayFn,
            final Function<Object[], String> groupIdFn, final Function<Object[], Integer> groupSeqFn) {
        this.reflected = reflected;
        this.config = config;
        this.typeFn = typeFn;
        this.correlIdFn = correlIdFn;
        this.ttlFn = ttlFn;
        this.delayFn = delayFn;
        this.groupIdFn = groupIdFn;
        this.groupSeqFn = groupSeqFn;
        this.propArgs = propArgs;
        this.propStatic = propStatic;
        this.bodyIndex = bodyIndex;
        this.bodyOf = bodyAs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JmsDispatch apply(final Object target, final Object[] args) {
        final var to = config.to();

        final var type = this.typeFn.apply(args);
        final var correlId = this.correlIdFn == null ? UUID.randomUUID().toString() : this.correlIdFn.apply(args);

        final var ttl = this.ttlFn == null ? config.ttl() : this.ttlFn.apply(args);
        final var delay = this.delayFn == null ? config.delay() : this.delayFn.apply(args);
        final var groupId = this.groupIdFn == null ? null : this.groupIdFn.apply(args);
        final var groupSeq = groupId == null || this.groupSeqFn == null ? 0 : this.groupSeqFn.apply(args);

        // Static first
        final var properties = new HashMap<String, Object>(propStatic);
        // Then arguments
        for (final var entry : propArgs.entrySet()) {
            final var argIndex = entry.getKey();
            final var propArg = entry.getValue();
            final var propName = propArg.name;
            final var propType = propArg.type();
            final var arg = args[argIndex];

            // Must have a property name for non-map values.
            if (!OneUtil.hasValue(propName) && !propType.isAssignableFrom(Map.class)) {
                throw new IllegalArgumentException(
                        "Un-defined property name on parameter " + reflected.getParameter(argIndex));
            }

            if (propType.isAssignableFrom(Map.class)) {
                // Skip null maps.
                if (arg != null) {
                    properties.putAll(((Map<String, Object>) arg));
                }
            } else {
                properties.put(propName, arg);
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
            public BodyOf<?> bodyOf() {
                return bodyOf;
            }

            @Override
            public At replyTo() {
                return config.replyTo();
            }

//
            @Override
            public Duration requestTimeout() {
                return config.dispatchReplyTimeout();
            }

            @Override
            public Duration ttl() {
                return ttl;
            }

            @Override
            public String groupId() {
                return groupId;
            }

            @Override
            public int groupSeq() {
                return groupSeq;
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

    public record PropertyArg(String name, Class<?> type) {
    }
}
