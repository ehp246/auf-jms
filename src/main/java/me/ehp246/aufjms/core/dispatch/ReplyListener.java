package me.ehp246.aufjms.core.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final static Logger LOGGER = LogManager.getLogger();

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

        LOGGER.atDebug().withMarker(AufJmsConstants.HEADERS).log("{}, {}", msg::correlationId, msg::type);
        LOGGER.atTrace().withMarker(AufJmsConstants.BODY).log("{}", msg::text);

        final var future = futureSupplier.get(msg.correlationId());

        if (future == null) {
            LOGGER.atTrace().log("{} not found", msg::correlationId);
        } else {
            future.complete(msg);
        }
    }
}
