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
import me.ehp246.aufjms.util.MockProxyConfig;
import me.ehp246.aufjms.util.TestUtil;

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

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(proxyConfig, captor.invocation()).type(),
                "should take arg");
    }

    @Test
    void type_arg_01() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_II(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_arg_02() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_III(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_arg_03() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_III(null);

        Assertions.assertEquals(null, dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_arg_04() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_II(null);

        Assertions.assertEquals(TypeCases.TYPE_I, dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_04() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type02();

        Assertions.assertEquals(TypeCases.TYPE_I, dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_05() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type02_I();

        Assertions.assertEquals("Type02_I", dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_arg_06() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_arg_07() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(null);

        final var dispatch = dispatchBuilder.get(proxyConfig, captor.invocation());

        Assertions.assertEquals(null, dispatch.type());
    }

    @Test
    void type_07() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type02();

        final var dispatch = dispatchBuilder.get(proxyConfig, captor.invocation());

        Assertions.assertEquals(TypeCases.TYPE_II, dispatch.type());
    }

    @Test
    void type_08() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type03();

        final var dispatch = dispatchBuilder.get(proxyConfig, captor.invocation());

        Assertions.assertEquals(TypeCases.TYPE_I, dispatch.type(), "should find annotated first");
    }

    @Test
    void type_method_09() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type04();

        Assertions.assertEquals("Type04", dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_type_09() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);
        captor.proxy().m01();

        Assertions.assertEquals("M01", dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_type_10() {
        final var captor = TestUtil.newCaptor(TypeCases.Case03.class);
        captor.proxy().m01();

        Assertions.assertEquals("Case03", dispatchBuilder.get(proxyConfig, captor.invocation()).type());
    }

    @Test
    void type_type_11() {
        final var captor = TestUtil.newCaptor(TypeCases.Case03.class);
        captor.proxy().m02();

        Assertions.assertEquals("M02", dispatchBuilder.get(proxyConfig, captor.invocation()).type(),
                "should follow the first annotated");
    }

    @Test
    void ttl_01() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().get();

        Assertions.assertEquals(null, dispatchBuilder.get(proxyConfig, captor.invocation()).ttl());
    }

    @Test
    void ttl_02() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().get();

        Assertions.assertEquals(Duration.ofDays(1).toMillis(), dispatchBuilder.get(new MockProxyConfig() {

            @Override
            public Duration ttl() {
                return Duration.ofDays(1);
            }

        }, captor.invocation()).ttl().toMillis());
    }

    @Test
    void ttl_03() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().getTtl01();

        Assertions.assertEquals(Duration.ofDays(1).toMillis(), dispatchBuilder.get(new MockProxyConfig() {

            @Override
            public Duration ttl() {
                return Duration.ofDays(1);
            }

        }, captor.invocation()).ttl().toMillis());
    }

    @Test
    void ttl_04() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().getTtl02();

        Assertions.assertEquals(Duration.ofSeconds(10).toMillis(), dispatchBuilder.get(new MockProxyConfig() {

            @Override
            public Duration ttl() {
                return Duration.ofDays(1);
            }

        }, captor.invocation()).ttl().toMillis());
    }

    @Test
    void ttl_05() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().getTtl03();

        Assertions.assertThrows(Exception.class,
                () -> dispatchBuilder.get(new MockProxyConfig(), captor.invocation()));
    }
}
