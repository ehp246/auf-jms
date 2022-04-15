package me.ehp246.aufjms.api.spi;

import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public enum Log4jContext {
    AufJmsDestination, AufJmsCorrelationId, AufJmsType;

    public static void set(final JmsMsg msg) {
        ThreadContext.put(Log4jContext.AufJmsDestination.name(), msg.destination().toString());
        ThreadContext.put(Log4jContext.AufJmsType.name(), msg.type());
        ThreadContext.put(Log4jContext.AufJmsCorrelationId.name(), msg.correlationId());
    }

    public static void clear() {
        for(final var value: Log4jContext.values()) {
            ThreadContext.remove(value.name());
        }
    }
}
