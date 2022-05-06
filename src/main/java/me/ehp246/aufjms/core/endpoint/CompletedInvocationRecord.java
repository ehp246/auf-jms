package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.BoundExecutable;
import me.ehp246.aufjms.api.endpoint.CompletedInvocation;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record CompletedInvocationRecord(JmsMsg msg, BoundExecutable target, Object returned) implements CompletedInvocation {

}
