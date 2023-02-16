package me.ehp246.aufjms.core.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DispatchLogger implements DispatchListener.OnDispatch, DispatchListener.PreSend,
        DispatchListener.PostSend, DispatchListener.OnException {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void onDispatch(final JmsDispatch dispatch) {
        LOGGER.atInfo().log("To={}, type={}, correlationId={}", dispatch::to, dispatch::type,
                dispatch::correlationId);

        LOGGER.atTrace().log("Properties: {}", dispatch::properties);
    }

    @Override
    public void preSend(final JmsDispatch dispatch, final JmsMsg msg) {
        LOGGER.atTrace().log("Body: {}", () -> msg == null ? "" : msg.text());
    }

    @Override
    public void postSend(final JmsDispatch dispatch, final JmsMsg msg) {
        LOGGER.atTrace().log("Sent");
    }

    @Override
    public void onException(final JmsDispatch dispatch, final JmsMsg msg, final Exception e) {
        LOGGER.atError().withThrowable(e).log("Failed: {}", e::getMessage);
    }
}
