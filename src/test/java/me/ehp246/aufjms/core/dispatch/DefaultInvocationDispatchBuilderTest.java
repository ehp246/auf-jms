package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.Invocation;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocationDispatchBuilderTest {
    private final static ByJmsProxyConfig proxyConfig = new ByJmsProxyConfig() {

        @Override
        public Duration ttl() {
            return null;
        }

        @Override
        public String destination() {
            return null;
        }

        @Override
        public String connection() {
            return null;
        }

        @Override
        public String replyTo() {
            return null;
        }
    };

    private DefaultInvocationDispatchBuilder dispatchBuilder = new DefaultInvocationDispatchBuilder(
            (con, dest) -> null);

    @SuppressWarnings("unchecked")
    private <T> T getCase(final Class<T> t, final Consumer<Invocation> consumer) {
        return (T) (Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { t },
                (proxy, method, args) -> {
                    consumer.accept(new Invocation() {

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
                    });
                    return null;
                }));
    }

    @Test
    void type_01() {
        final var argType = UUID.randomUUID().toString();

        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).type01(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(proxyConfig, invocation[0]).type());
    }

    @Test
    void type_arg_01() {
        final var argType = UUID.randomUUID().toString();

        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).type01_II(argType);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(argType, dispatch.type());
    }

    @Test
    void type_arg_02() {
        final var argType = UUID.randomUUID().toString();

        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).type01_III(argType);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(argType, dispatch.type());
    }

    @Test
    void type_arg_03() {
        final Invocation[] invocation = new Invocation[1];

        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).type01_III(null);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(null, dispatch.type());
    }

    @Test
    void type_arg_04() {
        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).type01_II(null);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(TypeTestCases.TYPE_I, dispatch.type());
    }

    @Test
    void type_04() {
        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).type02();

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(TypeTestCases.TYPE_I, dispatch.type());
    }

    @Test
    void type_05() {
        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).type02_I();

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals("Type02_I", dispatch.type());
    }

    @Test
    void type_arg_06() {
        final var argType = UUID.randomUUID().toString();

        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case02.class, i -> invocation[0] = i).type01(argType);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(argType, dispatch.type());
    }

    @Test
    void type_arg_07() {
        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case02.class, i -> invocation[0] = i).type01(null);

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(null, dispatch.type());
    }

    @Test
    void type_07() {
        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case02.class, i -> invocation[0] = i).type02();

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(TypeTestCases.TYPE_II, dispatch.type());
    }

    @Test
    void type_08() {
        final Invocation[] invocation = new Invocation[1];
        getCase(TypeTestCases.Case02.class, i -> invocation[0] = i).type03();

        final var dispatch = dispatchBuilder.get(proxyConfig, invocation[0]);

        Assertions.assertEquals(TypeTestCases.TYPE_I, dispatch.type());
    }

    @Test
    void type_method_09() {
        final Invocation[] invocation = new Invocation[1];

        getCase(TypeTestCases.Case02.class, i -> invocation[0] = i).type04();

        Assertions.assertEquals("Type04", dispatchBuilder.get(proxyConfig, invocation[0]).type());
    }

    @Test
    void type_type_09() {
        final Invocation[] invocation = new Invocation[1];

        getCase(TypeTestCases.Case01.class, i -> invocation[0] = i).m01();

        Assertions.assertEquals("Case01", dispatchBuilder.get(proxyConfig, invocation[0]).type());
    }

}
