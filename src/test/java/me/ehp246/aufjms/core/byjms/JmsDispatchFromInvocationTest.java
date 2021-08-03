package me.ehp246.aufjms.core.byjms;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQTopic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchFromInvocationTest {
    private final static String[] NAMES = new String[2];
    private final static Destination destination = new ActiveMQTopic();
    private final static String replyToName = UUID.randomUUID().toString();
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

        @Override
        public String replyTo() {
            return replyToName;
        }
    }, (con, dest) -> {
        NAMES[0] = con;
        NAMES[1] = dest;
        return destination;
    });

    @Test
    void test_01() {
        final var dispatch = fromInvocation.get(new JmsDispatchFromInvocationTestCase().getM01Invocation());

        Assertions.assertEquals(destination, dispatch.destination());
        Assertions.assertEquals("M01", dispatch.type());
        Assertions.assertEquals(true, dispatch.correlationId() != null);
        Assertions.assertEquals(334, dispatch.ttl());
        Assertions.assertEquals(true, dispatch.bodyValues().size() == 0);
        Assertions.assertEquals(destination, dispatch.replyTo());
        Assertions.assertEquals(null, dispatch.groupId());
        Assertions.assertEquals(null, dispatch.groupSeq());
    }

    @Test
    void destintationResolver_01() {
        fromInvocation.get(new JmsDispatchFromInvocationTestCase().getM01Invocation());

        Assertions.assertEquals(connectionName, NAMES[0]);
        Assertions.assertEquals(replyToName, NAMES[1]);
    }

    @Test
    void destintationResolver_02() throws NoSuchMethodException, SecurityException {
        final String[] names = new String[2];
        new JmsDispatchFromInvocation(new ByJmsProxyConfig() {

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

            @Override
            public String replyTo() {
                return null;
            }
        }, (con, dest) -> {
            names[0] = con;
            names[1] = dest;
            return destination;
        }).get(new JmsDispatchFromInvocationTestCase().getM01Invocation());

        Assertions.assertEquals(connectionName, names[0]);
        Assertions.assertEquals(destinationName, names[1]);
    }

    @Test
    void body_01() throws NoSuchMethodException, SecurityException {
        final var args = new ArrayList<>();
        args.add(null);
        final var dispatch = fromInvocation.get(new JmsDispatchFromInvocationTestCase().getM02Invocation(args));

        Assertions.assertEquals(1, dispatch.bodyValues().size());
        Assertions.assertEquals(null, dispatch.bodyValues().get(0));
        Assertions.assertThrows(Exception.class, () -> dispatch.bodyValues().clear());
    }

    @Test
    void body_02() throws NoSuchMethodException, SecurityException {
        final var now = Instant.now();
        final var dispatch = fromInvocation.get(new JmsDispatchFromInvocationTestCase().getM02Invocation(List.of(now)));

        Assertions.assertEquals(1, dispatch.bodyValues().size());
        Assertions.assertEquals(now, dispatch.bodyValues().get(0));
    }
}
