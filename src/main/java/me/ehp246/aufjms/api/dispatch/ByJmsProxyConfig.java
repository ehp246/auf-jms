package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;

import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 * @since 1.0
 */
public record ByJmsProxyConfig(At to, At replyTo, Duration ttl, Duration delay, String connectionFactory) {
    public ByJmsProxyConfig(At to) {
        this(to, null, null, null, null);
    }

    public ByJmsProxyConfig(At to, At replyTo) {
        this(to, replyTo, null, null, null);
    }

    public ByJmsProxyConfig(At to, Duration ttl) {
        this(to, null, ttl, null, null);
    }

    public ByJmsProxyConfig(At to, At replyTo, Duration ttl) {
        this(to, replyTo, ttl, null, null);
    }
}
