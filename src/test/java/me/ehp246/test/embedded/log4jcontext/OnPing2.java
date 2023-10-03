package me.ehp246.test.embedded.log4jcontext;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.OfLog4jContext;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.inbound.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(value = "Ping2", scope = InstanceScope.BEAN)
class OnPing2 {
    private final AtomicReference<CompletableFuture<Map<String, String>>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    public void invoke(final Order order, @OfLog4jContext @OfProperty final int accountId) {
        this.ref.get().complete(ThreadContext.getContext());
    }

    Map<String, String> take() throws InterruptedException, ExecutionException {
        final var received = this.ref.get().get();
        this.ref.set(new CompletableFuture<>());
        return received;
    }

    public record Order(@OfLog4jContext("OrderId") int id, @OfLog4jContext int amount) {
    }
}
