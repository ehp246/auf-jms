package me.ehp246.aufjms.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ForMsgExecutionExceptionTest {

    @Test
    void test() {
        Assertions.assertEquals(0, new ForMsgExecutionException(0).getCode());
        Assertions.assertEquals("0", new ForMsgExecutionException("0").getMessage());
        Assertions.assertEquals(null, new ForMsgExecutionException("0").getCode());
        Assertions.assertEquals(null, new ForMsgExecutionException(null, "0").getCode());
    }

}
