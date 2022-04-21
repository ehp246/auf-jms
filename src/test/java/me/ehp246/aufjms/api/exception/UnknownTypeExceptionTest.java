package me.ehp246.aufjms.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
class UnknownTypeExceptionTest {

    @Test
    void test() {
        final var msg = Mockito.mock(JmsMsg.class);

        Assertions.assertEquals(msg, new UnknownTypeException(msg).msg());
    }

}
