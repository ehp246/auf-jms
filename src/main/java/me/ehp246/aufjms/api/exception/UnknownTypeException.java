package me.ehp246.aufjms.api.exception;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public class UnknownTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final JmsMsg msg;

    public UnknownTypeException(final JmsMsg msg) {
        super("Unknown type: " + msg.type() + ", " + msg.correlationId());
        this.msg = msg;
    }

    public JmsMsg msg() {
        return this.msg;
    }
}
