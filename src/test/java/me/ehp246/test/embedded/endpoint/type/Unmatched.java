package me.ehp246.test.embedded.endpoint.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufjms.api.inbound.MsgConsumer;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
class Unmatched implements MsgConsumer {
    public final AtomicReference<CompletableFuture<JmsMsg>> ref = new AtomicReference<CompletableFuture<JmsMsg>>(
            new CompletableFuture<>());

    @Override
    public void accept(JmsMsg msg) {
        ref.get().complete(msg);
    }

}
