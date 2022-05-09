package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.endpoint.InvocationListener.CompletedListener;
import me.ehp246.aufjms.api.endpoint.InvocationListener.FailedInterceptor;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;

/**
 * @author Lei Yang
 *
 */
public sealed interface InvocationListener permits CompletedListener, FailedInterceptor {
    @FunctionalInterface
    public non-sealed interface CompletedListener extends InvocationListener {
        void onCompleted(final Completed completed);
    }

    @FunctionalInterface
    public non-sealed interface FailedInterceptor extends InvocationListener {
        void onFailed(Failed failed) throws Exception;
    }
}
