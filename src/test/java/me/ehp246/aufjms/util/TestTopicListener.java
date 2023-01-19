package me.ehp246.aufjms.util;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Message;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public class TestTopicListener {
    public static final String DESTINATION_NAME = "4f9aafa1-0b13-418d-84fc-074fc581574c";
    public static final String SUBSCRIPTION_NAME = "96bc8f8f-fe47-4ac5-8a74-2fac3ebb717f";

    private CompletableFuture<Message> received = new CompletableFuture<>();

    @JmsListener(destination = TestTopicListener.DESTINATION_NAME, subscription = SUBSCRIPTION_NAME)
    public void receive(final Message message) {
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
        final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}
