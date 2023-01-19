package me.ehp246.aufjms.api.jms;

import jakarta.jms.JMSException;
import jakarta.jms.JMSRuntimeException;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface JMSSupplier<V> {
    V get() throws JMSException;

    static <V> V invoke(final JMSSupplier<V> callable) {
        try {
            return callable.get();
        } catch (final JMSException e) {
            throw new JMSRuntimeException(e.getMessage(), e.getErrorCode(), e);
        }
    }
}