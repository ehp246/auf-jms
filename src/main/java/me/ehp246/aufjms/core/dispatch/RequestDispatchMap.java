package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public interface RequestDispatchMap extends ReplyFutureSupplier {
    /**
     * Add a new future to the map for the id.
     * <p>
     *
     * @throws IllegalArgumentException if the id exists in the map already.
     */
    CompletableFuture<JmsMsg> add(final String correlationId);

    /**
     * Removes the mapping.
     *
     * @return previously-mapped value. <code>null</code> if no existing value.
     */
    CompletableFuture<JmsMsg> remove(String correlationId);
}
