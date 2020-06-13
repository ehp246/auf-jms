package org.ehp246.aufjms.core.jms;

import java.util.Objects;
import java.util.function.Supplier;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.ehp246.aufjms.api.jms.MessageBuilder;
import org.ehp246.aufjms.api.jms.MessagePort;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.ehp246.aufjms.api.jms.MessageSupplier;
import org.ehp246.aufjms.api.jms.MsgSinkContext;
import org.ehp246.aufjms.util.ToMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class ConnectionPortProvider implements MessagePortProvider {
	private final static Logger LOGGER = LoggerFactory.getLogger(ConnectionPortProvider.class);

	private final Connection connection;
	private final MessageBuilder msgBuilder;

	public ConnectionPortProvider(final Connection connection, final MessageBuilder msgBuilder) {
		super();
		this.connection = Objects.requireNonNull(connection);
		this.msgBuilder = Objects.requireNonNull(msgBuilder);
	}

	@Override
	public MessagePort get(final Supplier<Destination> supplier) {
		return msgSupplier -> {
			final var destination = supplier.get();

			LOGGER.trace("Sending {} to {} ", msgSupplier.getCorrelationId(), destination.toString());

			try (final Session session = connection.createSession(true, Session.SESSION_TRANSACTED)) {
				final var context = new MsgSinkContext() {

					@Override
					public Session getSession() {
						return session;
					}

					@Override
					public MessageSupplier getMsgSupplier() {
						return msgSupplier;
					}
				};

				final var message = msgBuilder.build(context);

				try (final MessageProducer producer = session.createProducer(destination)) {

					producer.send(message);

					session.commit();

					LOGGER.trace("Sent {} ", msgSupplier.getCorrelationId());

					return ToMsg.from(message);
				}
			} catch (JMSException e) {
				LOGGER.error("Failed to take: to {}, type {}, correclation id {}", destination.toString(),
						msgSupplier.getType(), msgSupplier.getCorrelationId(), e);
				throw new RuntimeException(e);
			}
		};
	}
}
