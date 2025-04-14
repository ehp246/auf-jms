package me.ehp246.test.embedded.mdc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.OfMDC;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.inbound.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = "Ping2", scope = InstanceScope.BEAN)
public class OnPing2 {
    private final AtomicReference<CompletableFuture<Map<String, String>>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    public void apply(final Order order, @OfMDC @OfProperty final int accountId) {
        this.ref.get().complete(ThreadContext.getContext());
    }

    Map<String, String> take() throws InterruptedException, ExecutionException {
        final var received = this.ref.get().get();
        this.ref.set(new CompletableFuture<>());
        return received;
    }

    public record Order(@OfMDC("OrderId") int id, @OfMDC int amount) {
    }
}
