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
        this.completedRef.set(new CompletableFuture<>());
        this.completedRef.get().complete(completed);
    }

    @Override
    public void onInvoking(final BoundInvocable bound) {
        this.boundRef.set(new CompletableFuture<>());
        this.boundRef.get().complete(bound);
    }

    Completed getCompleted() throws InterruptedException, ExecutionException {
        return this.completedRef.get().get();
    }

    BoundInvocable getBound() throws InterruptedException, ExecutionException {
        return this.boundRef.get().get();
    }
}
