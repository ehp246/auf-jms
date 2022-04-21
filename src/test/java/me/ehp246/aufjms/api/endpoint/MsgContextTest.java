package me.ehp246.aufjms.api.endpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
class MsgContextTest {

    @Test
    void test() {
        Assertions.assertEquals(null, new MsgContext() {

            @Override
            public JmsMsg msg() {
                return null;
            }
        }.jmsContext());
    }

}
