package org.ehp246.aufjms.api.jms;

import java.util.HashMap;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 *
 * @param <T>
 */
public abstract class AbstractMessageBuilder<T extends Message> implements MessageBuilder {
	private final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageBuilder.class);

	private final ReplyDestinationSupplier replyTo;

	public AbstractMessageBuilder(final ReplyDestinationSupplier replyTo) {
		super();
		this.replyTo = replyTo;
	}

	protected abstract T createMessage(MsgSinkContext sinkContext);

	@Override
	public Message build(MsgSinkContext sinkContext) {
		final var message = this.createMessage(sinkContext);
		final var msgSupplier = sinkContext.getMsgSupplier();

		try {
			// Fill the customs first so the framework ones won't get over-written.
			final var map = Optional.ofNullable(msgSupplier.getPropertyMap()).orElseGet(HashMap<String, String>::new);
			for (String key : map.keySet()) {
				message.setStringProperty(key, map.get(key));
			}

			/*
			 * JMS headers
			 */
			message.setJMSType(msgSupplier.getType());
			message.setJMSCorrelationID(msgSupplier.getCorrelationId());
			message.setJMSReplyTo(this.replyTo.get());

			message.setStringProperty("JMSXGroupID", msgSupplier.getGroupId());
		} catch (JMSException e) {
			LOGGER.debug("Message builder failed: " + e.getMessage());

			throw new RuntimeException(e);
		}

		return message;
	}

}
