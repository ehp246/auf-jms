package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public interface CompletedInvocation {
    JmsMsg msg();

    BoundExecutable target();

    Object returned();
}
