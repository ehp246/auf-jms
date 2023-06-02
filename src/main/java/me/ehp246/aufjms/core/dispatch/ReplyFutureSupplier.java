package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ReplyFutureSupplier {
    /**
     * @return existing value if there is one. <code>null</code> if the key does not
     *         exist.
     */
    CompletableFuture<JmsMsg> get(String correlationId);
}
