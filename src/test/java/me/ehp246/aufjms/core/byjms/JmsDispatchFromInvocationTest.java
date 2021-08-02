package me.ehp246.aufjms.core.byjms;

import java.util.UUID;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQTopic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.core.reflection.ProxyInvocation;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchFromInvocationTest {
    private final static String[] names = new String[2];
    private final static Destination destination = new ActiveMQTopic();
    private final static String destinationName = UUID.randomUUID().toString();
    private final static String connectionName = UUID.randomUUID().toString();

    private JmsDispatchFromInvocation fromInvocation = new JmsDispatchFromInvocation(new ByJmsProxyConfig() {

        @Override
        public long ttl() {
            return 334;
        }

        @Override
        public String destination() {
            return destinationName;
        }

        @Override
        public String connection() {
            return connectionName;
        }
    }, (con, dest) -> {
        names[0] = con;
        names[1] = dest;
        return destination;
    });

    @Test
    void test_01() throws NoSuchMethodException, SecurityException {
        final var target = new JmsDispatchFromInvocationTestCase();

        final var dispatch = fromInvocation
                .from(new ProxyInvocation(JmsDispatchFromInvocationTestCase.class, target, target.getM01(), null));

        Assertions.assertEquals(destination, dispatch.destination());
        Assertions.assertEquals("M01", dispatch.type());
        Assertions.assertEquals(true, dispatch.correlationId() != null);
        Assertions.assertEquals(334, dispatch.ttl());
        Assertions.assertEquals(true, dispatch.bodyValues().size() == 0);
        Assertions.assertEquals(null, dispatch.replyTo());
        Assertions.assertEquals(null, dispatch.groupId());
        Assertions.assertEquals(null, dispatch.groupSeq());
    }

    @Test
    void names_01() throws NoSuchMethodException, SecurityException {
        final var target = new JmsDispatchFromInvocationTestCase();

        fromInvocation
                .from(new ProxyInvocation(JmsDispatchFromInvocationTestCase.class, target, target.getM01(), null));

        Assertions.assertEquals(connectionName, names[0]);
        Assertions.assertEquals(destinationName, names[1]);
    }

}
