package me.ehp246.broker.sb.dlq.dlq;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(value = "ThrowIt", scope = InstanceScope.BEAN)
public class LetterCollection {
    private CompletableFuture<Instant> ref = new CompletableFuture<>();

    @Invoking
    public void onLetter(final Instant instant) {
        this.ref.complete(instant);
    }
    public Instant take() throws InterruptedException, ExecutionException {
        final var instant = this.ref.get();
        this.ref = new CompletableFuture<>();
        return instant;
    }
}
