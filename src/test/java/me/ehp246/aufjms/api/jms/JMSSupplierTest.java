package me.ehp246.aufjms.api.jms;

import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
