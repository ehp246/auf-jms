package org.ehp246.aufjms.core.jms;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.ehp246.aufjms.api.jms.MessageCreator;
import org.ehp246.aufjms.api.jms.MsgPortContext;
import org.ehp246.aufjms.api.jms.MsgPortDestinationSupplier;
import org.ehp246.aufjms.api.jms.MsgPortProvider;
import org.ehp246.aufjms.api.jms.MsgPort;
import org.ehp246.aufjms.api.jms.MsgPropertyName;
import org.ehp246.aufjms.api.jms.MsgSupplier;
import org.ehp246.aufjms.core.util.ToMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lei Yang
 *
 */
public class ConnectionPortProvider implements MsgPortProvider {
	private final static Logger LOGGER = LoggerFactory.getLogger(ConnectionPortProvider.class);

	private final Connection connection;
	private final MessageCreator<?> msgBuilder;

	public ConnectionPortProvider(final Connection connection, final MessageCreator<?> msgBuilder) {
		super();
		this.connection = Objects.requireNonNull(connection);
		this.msgBuilder = Objects.requireNonNull(msgBuilder);
	}

	@Override
	public MsgPort get(final MsgPortDestinationSupplier supplier) {
		Objects.requireNonNull(supplier);

		return msgSupplier -> {
			final var destination = supplier.getTo();

			LOGGER.trace("Sending {}:{} to {} ", msgSupplier.getCorrelationId(), msgSupplier.getType(),
					destination.toString());

			try (final Session session = connection.createSession(true, Session.SESSION_TRANSACTED)) {
				final var context = new MsgPortContext() {

					@Override
					public Session getSession() {
						return session;
					}

					@Override
					public MsgSupplier getMsgSupplier() {
						return msgSupplier;
					}
				};

				final var message = msgBuilder.create(context);

				// Fill the customs first so the framework ones won't get over-written.
				final var map = Optional.ofNullable(msgSupplier.getPropertyMap())
						.orElseGet(HashMap<String, String>::new);
				for (final String key : map.keySet()) {
					message.setStringProperty(key, map.get(key));
				}

				message.setJMSDestination(supplier.getTo());
				message.setJMSReplyTo(supplier.getReplyTo());

				/*
				 * JMS headers
				 */
				message.setJMSType(msgSupplier.getType());
				message.setJMSCorrelationID(msgSupplier.getCorrelationId());
				message.setStringProperty(MsgPropertyName.GroupId, msgSupplier.getGroupId());

				/*
				 * Framework headers
				 */
				message.setStringProperty(MsgPropertyName.Invoking, msgSupplier.getInvoking());
				message.setBooleanProperty(MsgPropertyName.ServerThrown, msgSupplier.isException());

				try (final MessageProducer producer = session.createProducer(destination)) {

					producer.send(message);

					session.commit();

					LOGGER.trace("Sent {} ", msgSupplier.getCorrelationId());

					return ToMsg.from(message);
				}
			} catch (final JMSException e) {
				LOGGER.error("Failed to take: to {}, type {}, correclation id {}", destination.toString(),
						msgSupplier.getType(), msgSupplier.getCorrelationId(), e);
				throw new RuntimeException(e);
			}
		};
	}
}
