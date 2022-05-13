package me.ehp246.aufjms.core.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DispatchLogger implements DispatchListener.OnDispatch, DispatchListener.PreSend,
        DispatchListener.PostSend, DispatchListener.OnException {
    private final static Logger LOGGER = LogManager.getLogger(AufJmsConstants.DISPATCH_LOGTER);

    @Override
    public void onDispatch(JmsDispatch dispatch) {
        LOGGER.atInfo().log("Sending {}, {}, {}", dispatch::to, dispatch::type, dispatch::correlationId);
    }

    @Override
    public void preSend(JmsDispatch dispatch, JmsMsg msg) {
        LOGGER.atTrace().log("Sending {}, {}",
                () -> dispatch.body() == null ? null : dispatch.bodyAs().type(),
                msg::text);
    }

    @Override
    public void postSend(final JmsDispatch dispatch, final JmsMsg msg) {
        LOGGER.atInfo().log("Sent");
    }

    @Override
    public void onException(JmsDispatch dispatch, JmsMsg msg, Exception e) {
        LOGGER.atError().withThrowable(e).log("Failed: {}", e.getMessage());
    }
}
