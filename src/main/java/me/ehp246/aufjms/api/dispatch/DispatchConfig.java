package me.ehp246.aufjms.api.dispatch;

import me.ehp246.aufjms.api.jms.AtDestination;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface DispatchConfig {
    AtDestination destination();

    AtDestination replyTo();

    default String ttl() {
        return null;
    }

    default String context() {
        return null;
    }

}
