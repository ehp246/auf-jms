package me.ehp246.aufjms.api.jms;

import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface JMSSupplier<V> {
    V get() throws JMSException;

    static <V> V invoke(JMSSupplier<V> callable) {
        try {
            return callable.get();
        } catch (JMSException e) {
            throw new JMSRuntimeException(e.getMessage(), e.getErrorCode(), e);
        }
    }
}