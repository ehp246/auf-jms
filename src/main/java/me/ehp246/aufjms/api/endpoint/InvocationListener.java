package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.endpoint.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.endpoint.InvocationListener.OnFailed;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;

/**
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface InvocationListener permits OnCompleted, OnFailed {
    @FunctionalInterface
    public non-sealed interface OnCompleted extends InvocationListener {
        void onCompleted(final Completed completed);
    }

    @FunctionalInterface
    public non-sealed interface OnFailed extends InvocationListener {
        void onFailed(Failed failed) throws Exception;
    }
}
