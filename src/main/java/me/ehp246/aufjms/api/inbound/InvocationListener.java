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

    /**
     * If the listener would like to propagate the received exception, it should
     * throw the received explicitly. Otherwise, the received exception is
     * effectively caught by the listener.
     * <p>
     * Throwing an exception in one listener will break the invocation of
     * lower-ordered listeners.
     */
    @FunctionalInterface
    public non-sealed interface OnFailed extends InvocationListener {
        void onFailed(Failed failed) throws Throwable;
    }
}
