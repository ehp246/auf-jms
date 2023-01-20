package me.ehp246.aufjms.api.jms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.jms.JMSException;
import jakarta.jms.JMSRuntimeException;

/**
 * @author Lei Yang
 *
 */
class JMSSupplierTest {

    @Test
    void test() {
        Assertions.assertEquals(1, JMSSupplier.invoke(() -> 1));

        Assertions.assertThrows(JMSRuntimeException.class, () -> JMSSupplier.invoke(() -> {
            throw new JMSException("");
        }));
    }

}
