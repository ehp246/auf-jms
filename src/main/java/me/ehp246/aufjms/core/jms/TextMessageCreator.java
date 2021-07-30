package me.ehp246.aufjms.core.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.jms.MessageCreator;
import me.ehp246.aufjms.api.jms.MsgPortContext;
import me.ehp246.aufjms.api.jms.ToJsonMsgBody;

/**
 *
 * @author Lei Yang
 *
 * @param <T>
 */
public class TextMessageCreator implements MessageCreator<TextMessage> {
    private final Logger LOGGER = LogManager.getLogger(TextMessageCreator.class);

    private final ToJsonMsgBody<String> bodyWriter;

    public TextMessageCreator(final ToJsonMsgBody<String> bodyWriter) {
        super();
        this.bodyWriter = bodyWriter;
    }

    @Override
    public TextMessage create(final MsgPortContext context) {
        try {
            return context.getSession().createTextMessage(this.bodyWriter.to(context.getMsgSupplier().getBodyValues()));
        } catch (final JMSException e) {
            LOGGER.debug("Failed to create message: " + e.getMessage());

            throw new RuntimeException(e);
        }

    }

}
