package me.ehp246.aufjms.util;

import java.util.concurrent.CompletableFuture;

import javax.jms.Message;

import org.springframework.jms.annotation.JmsListener;

import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public class TestQueueListener {
    public static final String DESTINATION_NAME = "11603dc2-0791-4887-85ad-0a3720376b9b";

    private CompletableFuture<Message> received = new CompletableFuture<>();

    @JmsListener(destination = TestQueueListener.DESTINATION_NAME)
    public void receive(Message message) {
        received.complete(message);
    }

    public Message takeReceived() {
        return OneUtil.orThrow(() -> {
            final var message = this.received.get();
            this.received = new CompletableFuture<>();
            return message;
        });
    }
}
