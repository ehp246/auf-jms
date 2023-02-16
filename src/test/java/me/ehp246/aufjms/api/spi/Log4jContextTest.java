package me.ehp246.aufjms.api.spi;

import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
class Log4jContextTest {

    @Test
    void test() {
        Log4jContext.set((JmsDispatch) null);
        Log4jContext.clearDispatch();

        Log4jContext.set((JmsMsg) null);
        Log4jContext.clearMsg();
    }

}
