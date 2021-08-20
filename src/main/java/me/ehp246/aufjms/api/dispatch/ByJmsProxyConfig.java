package me.ehp246.aufjms.api.dispatch;

import me.ehp246.aufjms.api.jms.AtDestination;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface ByJmsProxyConfig {
    AtDestination destination();

    String ttl();

    String context();

    AtDestination replyTo();
}
