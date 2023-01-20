package me.ehp246.test.asb.eventgrid.storage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = ".*", scope = InstanceScope.BEAN)
class EventLogger {
    private final static Logger logger = LogManager.getLogger(EventLogger.class);

    private final CompletableFuture<JmsMsg> ref = new CompletableFuture<>();

    @Invoking
    public void perform(final JmsMsg msg, final EvenPayload payload) {
        logger.atDebug().log("Id: {}, type: {}", msg.correlationId(), msg.type());
        logger.atDebug().log("Headers: {}", msg.propertyNames());
        logger.atDebug().log("Text: {}", msg.text());
    }

    public JmsMsg take() throws InterruptedException, ExecutionException {
        return ref.get();
    }
}
