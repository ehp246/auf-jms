package me.ehp246.aufjms.api.dispatch;

import me.ehp246.aufjms.api.jms.To;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InvocationDispatchConfig {
    To to();

    To replyTo();

    default String ttl() {
        return null;
    }

    default String delay() {
        return null;
    }
}
