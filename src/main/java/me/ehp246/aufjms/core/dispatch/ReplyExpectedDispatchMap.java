package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public interface ReplyExpectedDispatchMap {
    CompletableFuture<JmsMsg> put(final String correlationId);

    CompletableFuture<JmsMsg> get(final String correlationId);
}
