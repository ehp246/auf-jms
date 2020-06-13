package org.ehp246.aufjms.core.jms;

import java.util.Objects;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.MessageBuilder;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.api.jms.MessagePipe;
import org.ehp246.aufjms.api.jms.MsgSinkContext;
import org.ehp246.aufjms.api.jms.MessageSupplier;
import org.ehp246.aufjms.util.ToMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class JmsConnectionPipe implements MessagePipe {
	private final static Logger LOGGER = LoggerFactory.getLogger(JmsConnectionPipe.class);

	private final Connection connection;
	private final DestinationNameResolver destinationResolver;
	private final MessageBuilder msgBuilder;

	public JmsConnectionPipe(final Connection connection, final DestinationNameResolver toResolver,
			final MessageBuilder msgBuilder) {
		super();
		this.connection = Objects.requireNonNull(connection);
		this.destinationResolver = Objects.requireNonNull(toResolver);
		this.msgBuilder = Objects.requireNonNull(msgBuilder);
	}

	@Override
	public Msg take(final MessageSupplier supplier) {
		try (final Session session = this.connection.createSession(true, Session.SESSION_TRANSACTED)) {
			final var context = new MsgSinkContext() {

				@Override
				public Session getSession() {
					return session;
				}

				@Override
				public MessageSupplier getMsgSupplier() {
					return supplier;
				}
			};

			final var message = this.msgBuilder.build(context);

			try (final MessageProducer producer = session
					.createProducer(this.destinationResolver.resolve(supplier.getTo()))) {
				LOGGER.trace("Sending {} to {} ", supplier.getCorrelationId(), supplier.getTo());

				producer.send(message);

				session.commit();

				LOGGER.trace("Sent {} ", supplier.getCorrelationId());

				return ToMsg.from(message);
			}
		} catch (JMSException e) {
			LOGGER.error("Failed to take: to {}, type {}, correclation id {}", supplier.getTo(), supplier.getType(),
					supplier.getCorrelationId(), e);
			throw new RuntimeException(e);
		}
	}
}
