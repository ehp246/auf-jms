package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
final class DefaultReplyExpectedDispatchMap implements ReplyExpectedDispatchMap {
    private final ConcurrentHashMap<String, CompletableFuture<JmsMsg>> map = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<JmsMsg> add(final String correlationId) {
        return map.compute(correlationId, (id, existing) -> {
            if (existing != null) {
                throw new IllegalArgumentException("Existing id: " + correlationId);
            }
            return new CompletableFuture<JmsMsg>();
        });
    }

    @Override
    public CompletableFuture<JmsMsg> get(final String correlationId) {
        return map.get(correlationId);
    }

    @Override
    public CompletableFuture<JmsMsg> remove(final String correlationId) {
        return map.remove(correlationId);
    }

    /**
     * For internal access.
     */
    ConcurrentHashMap<String, CompletableFuture<JmsMsg>> getMap() {
        return this.map;
    }
}
