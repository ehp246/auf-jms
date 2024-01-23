package me.ehp246.test.embedded.mdc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = "Ping", scope = InstanceScope.BEAN)
class OnPing {
    private CompletableFuture<Map<String, String>> ref = new CompletableFuture<>();

    void reset() {
        this.ref = new CompletableFuture<>();
    }

    public void invoke(final JmsMsg msg) {
        this.ref.complete(ThreadContext.getContext());
    }

    Map<String, String> take() throws InterruptedException, ExecutionException {
        final var received = this.ref.get();
        this.ref = new CompletableFuture<>();
        return received;
    }
}
