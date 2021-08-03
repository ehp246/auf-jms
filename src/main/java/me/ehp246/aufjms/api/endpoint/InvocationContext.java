package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocationContext {
    JmsMsg getMsg();
}
