package me.ehp246.aufjms.api.dispatch;

import javax.jms.TextMessage;

import me.ehp246.aufjms.api.exception.JmsDispatchFnException;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Defines life-cycle events supported by
 * {@linkplain JmsDispatchFn#send(JmsDispatch)}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface DispatchListener {
    /**
     * Invoked immediately after {@linkplain JmsDispatchFn#send(JmsDispatch)} is
     * called and before any JMS API invocations to construct and send the message.
     * <p>
     * This is the first event on the listener. It is very unlikely to be
     * interrupted by an exception.
     * 
     * @param dispatch
     */
    default void onDispatch(JmsDispatch dispatch) {
    }

    /**
     * Invoked after {@linkplain TextMessage} is created for the
     * {@linkplain JmsDispatch} and just before the message is sent.
     * <p>
     * This event will not trigger if an exception interrupts the construction
     * operations.
     * 
     * @param dispatch
     * @param msg
     */
    default void preSend(JmsDispatch dispatch, JmsMsg msg) {
    }

    /**
     * Invoked after the {@linkplain TextMessage} has been sent and before
     * {@linkplain JmsDispatchFn#send(JmsDispatch)} returns successfully.
     * 
     * @param dispatch
     * @param msg
     */
    default void postSend(JmsDispatch dispatch, JmsMsg msg) {
    }

    /**
     * Invoked when an {@linkplain Exception} has happened and before it is thrown
     * to the caller in a {@linkplain JmsDispatchFnException} interrupting
     * {@linkplain JmsDispatchFn#send(JmsDispatch)}.
     * 
     * @param dispatch
     * @param msg
     * @param e
     */
    default void onException(JmsDispatch dispatch, JmsMsg msg, Exception e) {
    }
}
