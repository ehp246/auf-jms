package me.ehp246.aufjms.api.exception;

import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.InvocableDispatcher;

/**
 * Indicates the invocation on {@linkplain BoundInvocable} has failed wrapping
 * the cause. Thrown by {@linkplain InvocableDispatcher}.
 *
 * @author Lei Yang
 */
public class BoundInvocationFailedException extends RuntimeException {
    private static final long serialVersionUID = -1977616923158789128L;

    public BoundInvocationFailedException(final Throwable cause) {
        super(cause);
    }
}
