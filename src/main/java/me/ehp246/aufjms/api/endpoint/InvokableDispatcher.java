package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvokableDispatcher {
    void dispatch(final JmsMsg msg);
}