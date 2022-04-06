package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 * @since 0.7.0
 */
public record FailedInvocation(JmsMsg msg, Executable target, Throwable thrown) {
}
