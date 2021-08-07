package me.ehp246.aufjms.core.byjms;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQTopic;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.Invocation;
import me.ehp246.aufjms.core.dispatch.DefaultInvocationDispatchBuilder;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocationDispatchProviderTest {
    private final static String[] NAMES = new String[2];
    private final static Destination destination = new ActiveMQTopic();
    private final static String replyToName = UUID.randomUUID().toString();
    private final static String destinationName = UUID.randomUUID().toString();
    private final static String connectionName = UUID.randomUUID().toString();
    private final static ByJmsProxyConfig proxyConfig = new ByJmsProxyConfig() {

        @Override
        public Duration ttl() {
            return Duration.ofMillis(334);
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
    };

    final Invocation[] invocation = new Invocation[1];
    final TypeTestCases.Case01 case01 = (TypeTestCases.Case01) (Proxy.newProxyInstance(this.getClass().getClassLoader(),
            new Class[] { TypeTestCases.Case01.class }, (proxy, method, args) -> {
                invocation[0] = new Invocation() {

                    @Override
                    public Object target() {
                        return proxy;
                    }

                    @Override
                    public Method method() {
                        return method;
                    }

                    @Override
                    public List<?> args() {
                        return args == null ? List.of() : Arrays.asList(args);
                    }
                };
                return null;
            }));

    private DefaultInvocationDispatchBuilder dispatchBuilder = new DefaultInvocationDispatchBuilder((con, dest) -> {
        NAMES[0] = con;
        NAMES[1] = dest;
        return destination;
    });

    @Test
    void test_01() {
        case01.m01();

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(destination, dispatch.destination());
        Assertions.assertEquals("M01", dispatch.type());
        Assertions.assertEquals(true, dispatch.correlationId() != null);
        Assertions.assertEquals(334, dispatch.ttl().toMillis());
        Assertions.assertEquals(true, dispatch.bodyValues().size() == 0);
        Assertions.assertEquals(destination, dispatch.replyTo());
        Assertions.assertEquals(null, dispatch.groupId());
        Assertions.assertEquals(null, dispatch.groupSeq());
    }

    @Test
    void type_01() {
        final var argType = UUID.randomUUID().toString();

        case01.type01(argType);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(argType, dispatch.type());
    }

    @Test
    void type_02() {
        final var argType = UUID.randomUUID().toString();

        case01.type01_II(argType);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(argType, dispatch.type());
    }

    @Test
    void type_03() {
        final var argType = UUID.randomUUID().toString();

        case01.type01_III(argType);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(argType, dispatch.type());
    }

    @Test
    void type_04() {
        case01.type02();

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(TypeTestCases.TYPE, dispatch.type());
    }

    @Test
    void type_05() {
        case01.type02_I();

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals("Type02_I", dispatch.type());
    }
    @Test
    void destintationResolver_01() {
        // fromInvocation.get(proxyConfig, new
        // TypeTestCases.Case01().getM01Invocation());

        Assertions.assertEquals(connectionName, NAMES[0]);
        Assertions.assertEquals(replyToName, NAMES[1]);
    }

    @Test
    void destintationResolver_02() throws NoSuchMethodException, SecurityException {
        final String[] names = new String[2];
        new DefaultInvocationDispatchBuilder((con, dest) -> {
            names[0] = con;
            names[1] = dest;
            return destination;
        }).get(new ByJmsProxyConfig() {

            @Override
            public Duration ttl() {
                return Duration.ofMillis(334);
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
        }, null);

        Assertions.assertEquals(connectionName, names[0]);
        Assertions.assertEquals(destinationName, names[1]);
    }

    @Test
    void body_01() throws NoSuchMethodException, SecurityException {
        final var args = new ArrayList<>();
        args.add(null);
        final var dispatch = dispatchBuilder.get(proxyConfig, null);

        Assertions.assertEquals(1, dispatch.bodyValues().size());
        Assertions.assertEquals(null, dispatch.bodyValues().get(0));
        Assertions.assertThrows(Exception.class, () -> dispatch.bodyValues().clear());
    }

    @Test
    void body_02() throws NoSuchMethodException, SecurityException {
        final var now = Instant.now();
        final var dispatch = dispatchBuilder.get(proxyConfig, null);

        Assertions.assertEquals(1, dispatch.bodyValues().size());
        Assertions.assertEquals(now, dispatch.bodyValues().get(0));
    }
}
