package me.ehp246.aufjms.api.dispatch;

import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InvocationDispatchConfig {
    At to();

    At replyTo();

    default String ttl() {
        return null;
    }

    default String delay() {
        return null;
    }
}
