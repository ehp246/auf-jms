package me.ehp246.aufjms.core.endpoint;

import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.util.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class NoopConsumerTest {

    @Test
    void test() {
        new NoopConsumer().accept(new MockJmsMsg());
    }

}
