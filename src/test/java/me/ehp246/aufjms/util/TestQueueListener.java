package me.ehp246.aufjms.util;

import java.util.concurrent.CompletableFuture;

import javax.jms.ConnectionFactory;
import javax.jms.Message;

import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

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

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(final ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}
