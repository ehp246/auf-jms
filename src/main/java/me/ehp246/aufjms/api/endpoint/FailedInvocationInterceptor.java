package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 * @since 0.7.0
 */
@FunctionalInterface
public interface FailedInvocationInterceptor {
    void accept(FailedInvocation failed) throws Exception;
}
