package me.ehp246.aufjms.api.inbound;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocableDispatcher {
    void dispatch(final Invocable invocable, final JmsMsg msg);
}
