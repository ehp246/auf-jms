package me.ehp246.aufjms.api.inbound;

import me.ehp246.aufjms.api.inbound.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnFailed;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;

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
        void onFailed(Failed failed) throws Throwable;
    }
}
