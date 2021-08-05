package me.ehp246.aufjms.core.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public final class DispatchLogger implements DispatchListener {
    private final static Logger LOGGER = LogManager.getLogger(DispatchLogger.class);

    @Override
    public void onDispatch(JmsMsg msg) {
        if (!LOGGER.isTraceEnabled()) {
            return;
        }

        LOGGER.trace("{}: {}", msg.correlationId(), msg.text());
    }

}
