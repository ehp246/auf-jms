package me.ehp246.aufjms.api.inbound;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableBinder {
    BoundInvocable bind(Invocable invocable, JmsMsg msg);
}
