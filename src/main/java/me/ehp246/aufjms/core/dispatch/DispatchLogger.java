package me.ehp246.aufjms.core.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger LOGGER = LoggerFactory.getLogger(DispatchLogger.class);

    @Override
    public void onDispatch(final JmsDispatch dispatch) {
        if (dispatch == null) {
            return;
        }
        LOGGER.atInfo().addMarker(AufJmsConstants.HEADERS).setMessage("{}, {}, {}")
                .addArgument(dispatch::correlationId).addArgument(dispatch::to)
                .addArgument(dispatch::type).log();
    }

    @Override
    public void preSend(final JmsDispatch dispatch, final JmsMsg msg) {
        if (dispatch == null || msg == null) {
            return;
        }
        LOGGER.atTrace().addMarker(AufJmsConstants.PROPERTIES).setMessage("{}")
                .addArgument(dispatch::properties).log();
        LOGGER.atTrace().addMarker(AufJmsConstants.BODY).setMessage("{}").addArgument(msg::text)
                .log();
    }

    @Override
    public void postSend(final JmsDispatch dispatch, final JmsMsg msg) {
    }

    @Override
    public void onException(final JmsDispatch dispatch, final JmsMsg msg, final Exception e) {
        if (e == null) {
            return;
        }
        LOGGER.atTrace().setCause(e).addMarker(AufJmsConstants.EXCEPTION).setMessage("{}")
                .addArgument(e::getMessage).log();
    }
}
