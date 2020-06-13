package org.ehp246.aufjms.core.configuration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.MessageBuilder;
import org.ehp246.aufjms.api.jms.MessagePipe;
import org.ehp246.aufjms.core.jms.JmsConnectionPipe;
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
	public MessagePipe msgPipe(Connection connection, DestinationNameResolver toResolver, MessageBuilder msgBuilder) {
		return new JmsConnectionPipe(connection, toResolver, msgBuilder);
	}
}
