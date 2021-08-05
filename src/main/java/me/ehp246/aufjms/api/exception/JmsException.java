package me.ehp246.aufjms.api.exception;

import javax.jms.JMSException;

/**
 * @author Lei Yang
 * @since 1.0
 */
public class JmsException extends RuntimeException {
    private static final long serialVersionUID = -1929177348024820503L;

    public JmsException(final JMSException cause) {
        super(cause);
    }
}
