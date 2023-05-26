package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
final class ReturningDispatchRepo {
    private final ConcurrentHashMap<String, CompletableFuture<JmsMsg>> map = new ConcurrentHashMap<>();

    public CompletableFuture<JmsMsg> put(final String correlationId) {
        final var future = new CompletableFuture<JmsMsg>();
        map.put(correlationId, future);
        return future;
    }

    public CompletableFuture<JmsMsg> get(final String correlationId) {
        return map.get(correlationId);
    }
}
