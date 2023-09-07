package me.ehp246.test.embedded.inbound.completed;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnInvoking;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;

/**
 * @author Lei Yang
 *
 */
class CompletedListener implements OnInvoking, OnCompleted {
    private final AtomicReference<CompletableFuture<BoundInvocable>> boundRef = new AtomicReference<>(
            new CompletableFuture<>());

    private final AtomicReference<CompletableFuture<Completed>> completedRef = new AtomicReference<>(
            new CompletableFuture<>());

    @Override
    public void onCompleted(final Completed completed) {
        this.completedRef.get().complete(completed);
    }

    @Override
    public void onInvoking(final BoundInvocable bound) {
        this.boundRef.get().complete(bound);
    }

    Completed takeCompleted() throws InterruptedException, ExecutionException {
        final var completed = this.completedRef.get().get();
        this.completedRef.set(new CompletableFuture<>());
        return completed;
    }

    BoundInvocable takeBound() throws InterruptedException, ExecutionException {
        final var bound = this.boundRef.get().get();
        this.boundRef.set(new CompletableFuture<>());
        return bound;
    }
}
