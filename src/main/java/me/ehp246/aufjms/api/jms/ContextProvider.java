package me.ehp246.aufjms.api.jms;

import javax.jms.JMSContext;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ContextProvider {
    JMSContext get(String name);
}
