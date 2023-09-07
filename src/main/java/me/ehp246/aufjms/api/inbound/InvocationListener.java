package me.ehp246.aufjms.api.inbound;

import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;

/**
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface InvocationListener {
    @FunctionalInterface
    public non-sealed interface OnInvoking extends InvocationListener {
        void onInvoking(final BoundInvocable bound);
    }

    @FunctionalInterface
    public non-sealed interface OnCompleted extends InvocationListener {
        void onCompleted(final Completed completed);
    }

    @FunctionalInterface
    public non-sealed interface OnFailed extends InvocationListener {
        void onFailed(Failed failed) throws Throwable;
    }
}
