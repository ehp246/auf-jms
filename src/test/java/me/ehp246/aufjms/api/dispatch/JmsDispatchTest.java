package me.ehp246.aufjms.api.dispatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchTest {

    @Test
    void test() {
        final var dispatch = new JmsDispatch() {
            
            @Override
            public At to() {
                return null;
            }
        };

        Assertions.assertEquals(null, dispatch.to());
        Assertions.assertEquals(null, dispatch.type());
        Assertions.assertEquals(null, dispatch.correlationId());
        Assertions.assertEquals(null, dispatch.body());
        Assertions.assertEquals(null, dispatch.replyTo());
    }

}
