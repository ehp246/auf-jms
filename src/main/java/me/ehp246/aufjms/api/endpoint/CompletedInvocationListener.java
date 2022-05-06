package me.ehp246.aufjms.api.endpoint;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface CompletedInvocationListener {
    void accept(final CompletedInvocation completed);
}
