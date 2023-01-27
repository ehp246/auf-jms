package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.List;

import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 * @since 1.0
 */
public record ByJmsProxyConfig(At to, At replyTo, Duration ttl, Duration delay, String connectionFactory,
        List<String> properties) {
    public ByJmsProxyConfig(final At to) {
        this(to, null, null, null, null, List.of());
    }

    public ByJmsProxyConfig(final At to, final At replyTo) {
        this(to, replyTo, null, null, null, List.of());
    }

    public ByJmsProxyConfig(final At to, final Duration ttl) {
        this(to, null, ttl, null, null, List.of());
    }

    public ByJmsProxyConfig(final At to, final At replyTo, final Duration ttl) {
        this(to, replyTo, ttl, null, null, List.of());
    }
}
