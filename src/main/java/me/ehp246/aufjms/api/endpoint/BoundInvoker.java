package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface BoundInvoker {
    Invoked apply(BoundInvocable bound);
}
