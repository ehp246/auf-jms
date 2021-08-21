package me.ehp246.aufjms.integration.endpoint.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.ConnectionFactory;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import me.ehp246.aufjms.api.annotation.EnableForJms;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableForJms
class AppConfig {
    @Bean
    public AtomicReference<CompletableFuture<Integer>> ref() {
        return new AtomicReference<CompletableFuture<Integer>>(new CompletableFuture<>());
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            final ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
