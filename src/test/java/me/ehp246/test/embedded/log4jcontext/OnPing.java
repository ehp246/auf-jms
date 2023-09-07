package me.ehp246.test.embedded.log4jcontext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(value = "Ping", scope = InstanceScope.BEAN)
class OnPing {
    private final AtomicReference<CompletableFuture<String>> ref = new AtomicReference<>(new CompletableFuture<>());

    public void invoke(final JmsMsg msg) {
        this.ref.get().complete(ThreadContext.get("OrderId"));
    }

    String take() throws InterruptedException, ExecutionException {
        final var received = this.ref.get().get();
        this.ref.set(new CompletableFuture<>());
        return received;
    }
}
