package org.ehp246.aufjms.core.configuration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.MessageCreator;
import org.ehp246.aufjms.core.jms.ConnectionPortProvider;
import org.springframework.context.annotation.Bean;

/**
 * Defines infrastructure bean's that are used by both front-end and back-end.
 * 
 * @author Lei Yang
 *
 */
public class ConnectionConfiguration {
	@Bean
	public Connection connection(ConnectionFactory connectionFactory) throws JMSException {
		return connectionFactory.createConnection();
	}

	@Bean
	public ConnectionPortProvider portProvider(Connection connection, DestinationNameResolver toResolver,
			MessageCreator<?> msgCreator) {
		return new ConnectionPortProvider(connection, msgCreator);
	}
}
