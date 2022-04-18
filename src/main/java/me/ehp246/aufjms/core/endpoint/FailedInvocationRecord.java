package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.FailedInvocation;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record FailedInvocationRecord(JmsMsg msg, Executable target, Throwable thrown) implements FailedInvocation {
}
