package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToInvoke {
    Invoked apply(BoundInvocable bound);
}
