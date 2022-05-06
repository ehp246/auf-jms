package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.CompletedInvocation;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record CompletedInvocationRecord(JmsMsg msg, BoundInvocable bound, Object returned) implements CompletedInvocation {

}
