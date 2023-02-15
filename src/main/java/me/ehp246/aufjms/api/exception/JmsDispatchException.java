package me.ehp246.aufjms.api.exception;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;

/**
 * Signals that an invocation to
 * {@linkplain JmsDispatchFn#send(me.ehp246.aufjms.api.jms.JmsDispatch)} has
 * failed. No message has been sent.
 * <p>
 * {@linkplain JmsDispatchFn} throws this exception only when a message coulnd't
 * be sent, not for other failures.
 *
 * @author Lei Yang
 * @since 2.0
 */
public class JmsDispatchException extends RuntimeException {
    private static final long serialVersionUID = -6208402038575832238L;

    public JmsDispatchException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JmsDispatchException(final Throwable cause) {
        super(cause);
    }

}
