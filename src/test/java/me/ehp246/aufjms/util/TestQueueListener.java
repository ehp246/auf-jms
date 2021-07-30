package me.ehp246.aufjms.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.jms.Message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * @author Lei Yang
 *
 */
@Service
public class TestQueueListener {
    private final CompletableFuture<Message> received = new CompletableFuture<>();

    @JmsListener(destination = "queue://" + AppConfig.TEST_QUEUE)
    public void receive(Message message) {
        received.complete(message);
    }

    public Message getReceived() throws InterruptedException, ExecutionException {
        return this.received.get();
    }
}
