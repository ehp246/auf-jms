package me.ehp246.aufjms.core.configuration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.jms.DestinationNameResolver;
import me.ehp246.aufjms.api.jms.MessageCreator;
import me.ehp246.aufjms.core.jms.DefaultDispatchFnProvider;

/**
 * Defines infrastructure bean's that are used by both front-end and back-end.
 *
 * @author Lei Yang
 *
 */
public class ConnectionConfiguration {
    @Bean
    public Connection connection(final ConnectionFactory connectionFactory) throws JMSException {
        return connectionFactory.createConnection();
    }

    @Bean
    public DefaultDispatchFnProvider portProvider(final Connection connection, final DestinationNameResolver toResolver,
            final MessageCreator<?> msgCreator) {
        return new DefaultDispatchFnProvider(connection, null);
    }
}
