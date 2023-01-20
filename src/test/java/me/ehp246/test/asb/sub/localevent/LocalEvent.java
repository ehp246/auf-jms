package me.ehp246.test.asb.sub.localevent;

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
public class LocalEvent {
    private final static Logger logger = LogManager.getLogger(LocalEvent.class);

    private final CompletableFuture<JmsMsg> ref = new CompletableFuture<>();

    @Invoking
    public void perform(final JmsMsg msg) {
        logger.atDebug().log("Id: {}", msg.correlationId());
    }

    public JmsMsg take() throws InterruptedException, ExecutionException {
        return ref.get();
    }
}
