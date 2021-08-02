package me.ehp246.aufjms.core.byjms;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.core.reflection.ProxyInvocation;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchFromInvocationTest {
    private JmsDispatchFromInvocation fromInvocation = new JmsDispatchFromInvocation(new ByJmsProxyConfig() {
        private final String destination = UUID.randomUUID().toString();
        private final String connection = UUID.randomUUID().toString();

        @Override
        public long ttl() {
            return 334;
        }

        @Override
        public String destination() {
            return destination;
        }

        @Override
        public String connection() {
            return connection;
        }
    });

    @Test
    void test_01() throws NoSuchMethodException, SecurityException {
        final var target = new JmsDispatchFromInvocationTestCase();

        final var dispatch = fromInvocation
                .from(new ProxyInvocation(JmsDispatchFromInvocationTestCase.class, target, target.getM01(), null));

        // Assertions.assertEquals("", dispatch.destination());
        Assertions.assertEquals("M01", dispatch.type());
        Assertions.assertEquals(true, dispatch.correlationId() != null);
        Assertions.assertEquals(334, dispatch.ttl());
        Assertions.assertEquals(true, dispatch.bodyValues().size() == 0);
        Assertions.assertEquals(null, dispatch.replyTo());
        Assertions.assertEquals(null, dispatch.groupId());
        Assertions.assertEquals(null, dispatch.groupSeq());
    }

}
