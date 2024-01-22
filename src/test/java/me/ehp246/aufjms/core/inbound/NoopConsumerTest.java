package me.ehp246.aufjms.core.inbound;

import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.core.inbound.NoOpConsumer;
import me.ehp246.test.mock.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class NoopConsumerTest {

    @Test
    void test() {
        new NoOpConsumer().accept(new MockJmsMsg());
    }

}
