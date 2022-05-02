package me.ehp246.aufjms.api.dispatch;

import javax.jms.TextMessage;

import me.ehp246.aufjms.api.dispatch.DispatchListener.OnDispatch;
import me.ehp246.aufjms.api.dispatch.DispatchListener.OnException;
import me.ehp246.aufjms.api.dispatch.DispatchListener.PostSend;
import me.ehp246.aufjms.api.dispatch.DispatchListener.PreSend;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Defines life-cycle events supported by
 * {@linkplain JmsDispatchFn#send(JmsDispatch)}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface DispatchListener permits OnDispatch, PreSend, PostSend, OnException {
    /**
     * Invoked immediately after {@linkplain JmsDispatchFn#send(JmsDispatch)} is
     * called and before any JMS API invocations to construct and send the message.
     * <p>
     * This is the first event on the listener. It is very unlikely to be
     * interrupted by an exception unless {@code dispatch} is {@code null}.
     */
    @FunctionalInterface
    non-sealed interface OnDispatch extends DispatchListener {
        void onDispatch(JmsDispatch dispatch);
    }

    /**
     * Invoked after {@linkplain TextMessage} is created for the
     * {@linkplain JmsDispatch} and just before the message is sent.
     * <p>
     * This event will not trigger if an exception interrupts the construction
     * operations.
     */
    @FunctionalInterface
    non-sealed interface PreSend extends DispatchListener {
        void preSend(JmsDispatch dispatch, JmsMsg msg);
    }

    /**
     * Invoked after the {@linkplain TextMessage} has been sent and before
     * {@linkplain JmsDispatchFn#send(JmsDispatch)} returns successfully.
     */
    @FunctionalInterface
    non-sealed interface PostSend extends DispatchListener {
        void postSend(JmsDispatch dispatch, JmsMsg msg);
    }

    /**
     * Invoked when an {@linkplain Exception} has happened and before it is thrown
     * to the caller interrupting {@linkplain JmsDispatchFn#send(JmsDispatch)}.
     * <p>
     * {@code msg} might be {@code null} depending on the cause.
     * <p>
     * If an exception happens inside the listener, it will be re-thrown as is. It
     * would be a good practice to set the cause to {@code e} when throwing from the
     * listener.
     */
    @FunctionalInterface
    non-sealed interface OnException extends DispatchListener {
        void onException(JmsDispatch dispatch, JmsMsg msg, Exception e);
    }
}
