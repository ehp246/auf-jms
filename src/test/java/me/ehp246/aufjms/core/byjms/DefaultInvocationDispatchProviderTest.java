package me.ehp246.aufjms.core.byjms;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;

import me.ehp246.aufjms.api.dispatch.InvocationDispatchConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.jms.To;
import me.ehp246.aufjms.api.jms.ToQueue;
import me.ehp246.aufjms.core.dispatch.DefaultInvocationDispatchBuilder;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocationDispatchProviderTest {
    private final static String[] NAMES = new String[2];
    private final static String replyToName = UUID.randomUUID().toString();
    private final static String destinationName = UUID.randomUUID().toString();
    private final static String connectionName = UUID.randomUUID().toString();
    private final static To destination = new ToQueue() {

        @Override
        public String name() {
            return destinationName;
        }
    };

    private final static InvocationDispatchConfig proxyConfig = new InvocationDispatchConfig() {

        @Override
        public String ttl() {
            return Duration.ofMillis(334).toString();
        }

        @Override
        public To to() {
            return destination;
        }

        @Override
        public To replyTo() {
            return null;
        }
    };

    private InvocationDispatchBuilder dispatchBuilder = null;

    void destintationResolver_01() {
        // fromInvocation.get(proxyConfig, new
        // TypeTestCases.Case01().getM01Invocation());

        Assertions.assertEquals(connectionName, NAMES[0]);
        Assertions.assertEquals(replyToName, NAMES[1]);
    }

    void propertyResolver_02() throws NoSuchMethodException, SecurityException {
        final String[] names = new String[2];
        new DefaultInvocationDispatchBuilder((dest) -> {
            names[1] = dest;
            return destinationName;
        }).get(null, new InvocationDispatchConfig() {

            @Override
            public String ttl() {
                return Duration.ofMillis(334).toString();
            }

            @Override
            public To to() {
                return null;
            }

            @Override
            public To replyTo() {
                return null;
            }
        });

        Assertions.assertEquals(connectionName, names[0]);
        Assertions.assertEquals(destinationName, names[1]);
    }

    void body_01() throws NoSuchMethodException, SecurityException {
        final var args = new ArrayList<>();
        args.add(null);
        final var dispatch = dispatchBuilder.get(null, proxyConfig);

        Assertions.assertEquals(1, dispatch.bodyValues().size());
        Assertions.assertEquals(null, dispatch.bodyValues().get(0));
        Assertions.assertThrows(Exception.class, () -> dispatch.bodyValues().clear());
    }

    void body_02() throws NoSuchMethodException, SecurityException {
        final var now = Instant.now();
        final var dispatch = dispatchBuilder.get(null, proxyConfig);

        Assertions.assertEquals(1, dispatch.bodyValues().size());
        Assertions.assertEquals(now, dispatch.bodyValues().get(0));
    }
}
