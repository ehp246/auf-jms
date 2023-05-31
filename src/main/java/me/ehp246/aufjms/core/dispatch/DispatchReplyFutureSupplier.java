package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface DispatchReplyFutureSupplier {
    CompletableFuture<JmsMsg> get(String correlationId);s
}
