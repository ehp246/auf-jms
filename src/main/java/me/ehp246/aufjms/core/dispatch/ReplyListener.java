package me.ehp246.aufjms.core.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.SessionAwareMessageListener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 *
 */
final class ReplyListener implements SessionAwareMessageListener<Message> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReplyListener.class);

    private final ReplyFutureSupplier futureSupplier;

    ReplyListener(final ReplyFutureSupplier futureSupplier) {
        super();
        this.futureSupplier = futureSupplier;
    }

    @Override
    public void onMessage(final Message message, final Session session) throws JMSException {
        if (!(message instanceof final TextMessage textMessage)) {
            throw new IllegalArgumentException("Un-supported message type");
        }

        final var msg = TextJmsMsg.from(textMessage);

        LOGGER.atDebug().addMarker(AufJmsConstants.HEADERS).setMessage("{}, {}")
                .addArgument(msg::correlationId).addArgument(msg::type).log();
        LOGGER.atTrace().addMarker(AufJmsConstants.BODY).setMessage("{}").addArgument(msg::text)
                .log();

        final var future = futureSupplier.get(msg.correlationId());

        if (future == null) {
            LOGGER.atTrace().setMessage("{} not found, ignored").addArgument(msg::correlationId)
                    .log();
        } else {
            future.complete(msg);
        }
    }
}
