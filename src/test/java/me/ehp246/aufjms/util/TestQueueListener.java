package me.ehp246.aufjms.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.jms.Message;

import org.springframework.jms.annotation.JmsListener;

/**
 * @author Lei Yang
 *
 */
public class TestQueueListener {
    private final CompletableFuture<Message> received = new CompletableFuture<>();

    @JmsListener(destination = UtilConfig.TEST_QUEUE)
    public void receive(Message message) {
        received.complete(message);
    }

    public Message getReceived() throws InterruptedException, ExecutionException {
        return this.received.get();
    }
}
