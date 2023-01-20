package me.ehp246.test;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public class TestQueueListener {
    private static final Logger logger = LogManager.getLogger();

    public static final String DESTINATION_NAME = "11603dc2-0791-4887-85ad-0a3720376b9b";

    private CompletableFuture<Message> received;

    @JmsListener(destination = TestQueueListener.DESTINATION_NAME)
    public void receive(final Message message) throws JMSException {
        if (received == null) {
            logger.atInfo().log("Ignoring {} {} {}", message.getJMSType(), message.getJMSCorrelationID(),
                    message.getJMSDestination().toString());
            return;
        }
        received.complete(message);
    }

    public void reset() {
        this.received = new CompletableFuture<>();
    }

    public Message takeReceived() {
        return OneUtil.orThrow(() -> {
            final var message = this.received.get();
            this.received = null;
            return message;
        });
    }
}
