package me.ehp246.aufjms.core.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DispatchLogger implements DispatchListener.OnDispatch, DispatchListener.PreSend,
        DispatchListener.PostSend, DispatchListener.OnException {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void onDispatch(final JmsDispatch dispatch) {
        if (dispatch == null) {
            return;
        }
        LOGGER.atInfo().withMarker(AufJmsConstants.HEADERS).log("{}, {}, {}", dispatch::correlationId, dispatch::to,
                dispatch::type);
    }

    @Override
    public void preSend(final JmsDispatch dispatch, final JmsMsg msg) {
        if (dispatch == null) {
            return;
        }
        LOGGER.atTrace().withMarker(AufJmsConstants.PROPERTIES).log("{}", dispatch::properties);
        LOGGER.atTrace().withMarker(AufJmsConstants.BODY).log("{}",
                () -> msg == null || msg.text() == null ? "" : msg.text());
    }

    @Override
    public void postSend(final JmsDispatch dispatch, final JmsMsg msg) {
    }

    @Override
    public void onException(final JmsDispatch dispatch, final JmsMsg msg, final Exception e) {
        if (e == null) {
            return;
        }
        LOGGER.atTrace().withThrowable(e).withMarker(AufJmsConstants.EXCEPTION).log("{}", e::getMessage);
    }
}
