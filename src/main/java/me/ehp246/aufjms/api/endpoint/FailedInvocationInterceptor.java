package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 * @since 0.7.0
 */
public interface FailedInvocationInterceptor {
    void accept(FailedInvocation failed);
}