package me.ehp246.aufjms.api.exception;

/**
 * @author Lei Yang
 *
 */
public final class JmsDispatchFnException extends RuntimeException {
    private static final long serialVersionUID = -4491326463167427192L;

    public JmsDispatchFnException(String message) {
        super(message);
    }

    public JmsDispatchFnException(Throwable cause) {
        super(cause);
    }
}
