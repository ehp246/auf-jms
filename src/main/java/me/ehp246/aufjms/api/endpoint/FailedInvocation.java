package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 * @since 0.7.0
 */
public interface FailedInvocation {
    JmsMsg msg();

    BoundInvocable bound();

    Throwable thrown();
}
