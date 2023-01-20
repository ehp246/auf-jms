package me.ehp246.aufjms.core.endpoint;

import org.junit.jupiter.api.Test;

import me.ehp246.test.mock.MockJmsMsg;

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
