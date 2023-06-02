package me.ehp246.aufjms.api.exception;

/**
 * @author Lei Yang
 *
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
