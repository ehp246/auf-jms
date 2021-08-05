package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface ByJmsProxyConfig {
    String destination();

    Duration ttl();

    String connection();

    String replyTo();
}
