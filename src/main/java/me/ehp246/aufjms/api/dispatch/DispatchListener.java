package me.ehp246.aufjms.api.dispatch;

import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.dispatch.DispatchListener.OnDispatch;
import me.ehp246.aufjms.api.dispatch.DispatchListener.OnException;
import me.ehp246.aufjms.api.dispatch.DispatchListener.PostSend;
import me.ehp246.aufjms.api.dispatch.DispatchListener.PreSend;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Defines life-cycle events supported by
 * {@linkplain JmsDispatchFn#send(JmsDispatch)}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface DispatchListener permits OnDispatch, PreSend, PostSend, OnException {
    @FunctionalInterface
    non-sealed interface OnDispatch extends DispatchListener {
        /**
         * Invoked immediately after {@linkplain JmsDispatchFn#send(JmsDispatch)} is
         * called and before any JMS API invocations to construct and send the message.
         * <p>
         * This is the first event on the listener. It is very unlikely to be
         * interrupted by an exception unless {@code dispatch} is {@code null}.
         */
        void onDispatch(JmsDispatch dispatch);
    }

    @FunctionalInterface
    non-sealed interface PreSend extends DispatchListener {
        /**
         * Invoked after a {@linkplain TextMessage} has been successfully constructed
         * for the {@linkplain JmsDispatch} and just before the message is sent.
         * <p>
         * This event will not trigger if an exception interrupts the construction
         * operations.
         */
        void preSend(JmsDispatch dispatch, JmsMsg msg);
    }

    @FunctionalInterface
    non-sealed interface PostSend extends DispatchListener {
        /**
         * Invoked after the {@linkplain TextMessage} has been sent and before
         * {@linkplain JmsDispatchFn#send(JmsDispatch)} returns successfully.
         */
        void postSend(JmsDispatch dispatch, JmsMsg msg);
    }

    @FunctionalInterface
    non-sealed interface OnException extends DispatchListener {
        /**
         * Invoked when an {@linkplain Exception} has happened and before it is thrown
         * to the caller interrupting {@linkplain JmsDispatchFn#send(JmsDispatch)}.
         * <p>
         * {@code msg} might be {@code null} depending on the cause.
         */
        void onException(JmsDispatch dispatch, JmsMsg msg, Exception e);
    }
}
