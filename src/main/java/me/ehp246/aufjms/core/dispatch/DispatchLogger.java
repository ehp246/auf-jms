package me.ehp246.aufjms.core.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DispatchLogger implements DispatchListener.PostSend {
    private final static Logger LOGGER = LogManager.getLogger(DispatchLogger.class);

    @Override
    public void postSend(final JmsDispatch dispatch, final JmsMsg msg) {
        LOGGER.atDebug().log("{}, {}, {}", () -> msg.destination().toString(), msg::type, msg::correlationId);
        LOGGER.atTrace().log("{}", msg::text);
    }
}
