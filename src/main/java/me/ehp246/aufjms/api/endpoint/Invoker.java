package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface Invoker {
    InvocationOutcome invoke(BoundInvocable bound);
}
