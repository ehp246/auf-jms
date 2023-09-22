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
     * When an invocation fails on a {@linkplain BoundInvocable},
     * {@linkplain InvocableDispatcher} makes the best effort to call all
     * {@linkplain OnFailed} listeners in turn passing in the failure.
     * <p>
     * If a {@linkplain OnFailed} throws an exception, the exception will not be
     * propagated. Instead it will be added to the
     * {@linkplain Throwable#getSuppressed()} of the invocation failure which will
     * be passed to the next {@linkplain OnFailed}.
     * <p>
     * After all {@linkplain OnFailed} have been executed, the original invocation
     * failure will be thrown with suppressed exceptions from the listeners.
     */
    @FunctionalInterface
    public non-sealed interface OnFailed extends InvocationListener {
        void onFailed(Failed failed);
    }
}
