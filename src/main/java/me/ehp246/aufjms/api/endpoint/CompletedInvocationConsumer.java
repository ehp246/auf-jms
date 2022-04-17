package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface CompletedInvocationConsumer {
    void accept(final CompletedInvocation completed);
}
