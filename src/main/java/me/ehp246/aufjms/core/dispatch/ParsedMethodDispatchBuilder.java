package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatch.BodyAs;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.core.reflection.ReflectedProxyMethod;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class ParsedMethodDispatchBuilder {
    private final ReflectedProxyMethod reflected;
    private final ByJmsProxyConfig config;
    private final Function<Object[], String> typeFn;
    private final Function<Object[], String> correlIdFn;
    private final Function<Object[], String> groupIdFn;
    private final Function<Object[], Integer> groupSeqFn;
    private final Function<Object[], Duration> ttlFn;
    private final Function<Object[], Duration> delayFn;
    private final int[] propertyArgs;
    private final String[] propertyNames;
    private final Class<?>[] propertyTypes;
    private final int bodyIndex;
    private final BodyAs bodyAs;

    ParsedMethodDispatchBuilder(final ReflectedProxyMethod reflected, final ByJmsProxyConfig config,
            final Function<Object[], String> typeFn, final Function<Object[], String> correlIdFn, final int bodyIndex,
            BodyAs bodyAs, final int[] propertyArgs, final Class<?>[] propertyTypes, String[] propertyNames,
            final Function<Object[], Duration> ttlFn, final Function<Object[], Duration> delayFn,
            final Function<Object[], String> groupIdFn, final Function<Object[], Integer> groupSeqFn) {
        this.reflected = reflected;
        this.config = config;
        this.typeFn = typeFn;
        this.correlIdFn = correlIdFn;
        this.ttlFn = ttlFn;
        this.delayFn = delayFn;
        this.groupIdFn = groupIdFn;
        this.groupSeqFn = groupSeqFn;
        this.propertyArgs = propertyArgs;
        this.propertyNames = propertyNames;
        this.propertyTypes = propertyTypes;
        this.bodyIndex = bodyIndex;
        this.bodyAs = bodyAs;
    }

    @SuppressWarnings("unchecked")
    public JmsDispatch apply(final Object target, final Object[] args) {
        final var to = config.to();

        final var type = this.typeFn.apply(args);
        final var correlId = this.correlIdFn == null ? UUID.randomUUID().toString() : this.correlIdFn.apply(args);

        final var ttl = this.ttlFn == null ? config.ttl() : this.ttlFn.apply(args);
        final var delay = this.delayFn == null ? config.delay() : this.delayFn.apply(args);
        final var groupId = this.groupIdFn == null ? null : this.groupIdFn.apply(args);
        final var groupSeq = groupId == null || this.groupSeqFn == null ? 0 : this.groupSeqFn.apply(args);

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
}
