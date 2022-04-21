package me.ehp246.aufjms.api.exception;

/**
 * @author Lei Yang
 *
 */
public final class ForMsgExecutionException extends RuntimeException implements ExecutionThrown {
    private static final long serialVersionUID = 1L;
    private final Integer code;

    /**
     * @param message
     */
    public ForMsgExecutionException(final String message) {
        super(message);
        code = null;
    }

    /**
     * @param code
     */
    public ForMsgExecutionException(final Integer code) {
        super();
        this.code = code;
    }

    public ForMsgExecutionException(final Integer code, final String message) {
        super(message);
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }

}
