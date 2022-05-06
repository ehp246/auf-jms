package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.FailedInvocation;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record FailedInvocationRecord(JmsMsg msg, BoundInvocable bound, Throwable thrown) implements FailedInvocation {
}
