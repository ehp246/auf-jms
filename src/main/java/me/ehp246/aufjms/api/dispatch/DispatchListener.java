package me.ehp246.aufjms.api.dispatch;

import javax.jms.JMSContext;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Called after a {@linkplain TextMessage} has been sent on a
 * {@linkplain JMSContext} for a {@linkplain JmsDispatch}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface DispatchListener {
    default void preSend(JmsDispatch dispatch, JmsMsg msg) {
    }

    default void postSend(JmsDispatch dispatch, JmsMsg msg) {
    }

    default void onException(JmsDispatch dispatch, JmsMsg msg, Exception e) {
    }
}
