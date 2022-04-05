package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
public interface FailedInvocationConsumer {
    void accept(FailedInvocation failed);
}
