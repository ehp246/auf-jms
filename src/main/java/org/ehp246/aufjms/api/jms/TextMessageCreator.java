package org.ehp246.aufjms.api.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 *
 * @param <T>
 */
public class TextMessageCreator implements MessageCreator<TextMessage> {
	private final Logger LOGGER = LoggerFactory.getLogger(TextMessageCreator.class);

	private final ToBody<String> bodyWriter;

	public TextMessageCreator(final ToBody<String> bodyWriter) {
		super();
		this.bodyWriter = bodyWriter;
	}

	@Override
	public TextMessage create(MsgPortContext context) {
		try {
			return context.getSession()
					.createTextMessage(this.bodyWriter.to(context.getMsgSupplier().getBodyValues()));
		} catch (JMSException e) {
			LOGGER.debug("Failed to create message: " + e.getMessage());

			throw new RuntimeException(e);
		}

	}

}
