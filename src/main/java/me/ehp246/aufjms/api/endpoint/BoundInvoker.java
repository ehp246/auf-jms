package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface BoundInvoker {
    InvocationOutcome apply(BoundInvocable bound);
}
