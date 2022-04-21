package me.ehp246.aufjms.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class JmsExceptionTest {

    @Test
    void test() {
        Assertions.assertEquals(null, new JmsException(null).getCause());
    }

}
