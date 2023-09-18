package me.ehp246.test.embedded.inbound.property;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.inbound.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(scope = InstanceScope.BEAN)
class NamedIntCase {
    private AtomicReference<CompletableFuture<Integer>> ref;

    public NamedIntCase reset() {
        this.ref = new AtomicReference<>(new CompletableFuture<>());
        return this;
    }

    public void invoke(@OfProperty final int named) {
        ref.get().complete(named);
    }

    public int getValue() throws InterruptedException, ExecutionException {
        return this.ref.get().get();
    }
}
