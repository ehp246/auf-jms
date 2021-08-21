package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.DispatchConfig;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.util.MockProxyConfig;
import me.ehp246.aufjms.util.TestUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocationDispatchBuilderTest {
    private final static AtDestination at = new AtDestination() {

        @Override
        public String name() {
            return "";
        }
    };
    private final static DispatchConfig proxyConfig = new DispatchConfig() {

        @Override
        public AtDestination destination() {
            return at;
        }

        @Override
        public String context() {
            return "";
        }

        @Override
        public AtDestination replyTo() {
            return at;
        }
    };

    private DefaultInvocationDispatchBuilder dispatchBuilder = new DefaultInvocationDispatchBuilder(
            String::toString);

    @Test
    void type_01() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(captor.invocation(), proxyConfig).type(),
                "should take arg");
    }

    @Test
    void type_arg_01() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_II(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_arg_02() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_III(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_arg_03() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_III(null);

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_arg_04() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01_II(null);

        Assertions.assertEquals(TypeCases.TYPE_I, dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_04() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type02();

        Assertions.assertEquals(TypeCases.TYPE_I, dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_05() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type02_I();

        Assertions.assertEquals("Type02_I", dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_arg_06() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(argType);

        Assertions.assertEquals(argType, dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_arg_07() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(null);

        final var dispatch = dispatchBuilder.get(captor.invocation(), proxyConfig);

        Assertions.assertEquals(null, dispatch.type());
    }

    @Test
    void type_07() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type02();

        final var dispatch = dispatchBuilder.get(captor.invocation(), proxyConfig);

        Assertions.assertEquals(TypeCases.TYPE_II, dispatch.type());
    }

    @Test
    void type_08() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type03();

        final var dispatch = dispatchBuilder.get(captor.invocation(), proxyConfig);

        Assertions.assertEquals(TypeCases.TYPE_I, dispatch.type(), "should find annotated first");
    }

    @Test
    void type_method_09() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type04();

        Assertions.assertEquals("Type04", dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_type_09() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);
        captor.proxy().m01();

        Assertions.assertEquals("M01", dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_type_10() {
        final var captor = TestUtil.newCaptor(TypeCases.Case03.class);
        captor.proxy().m01();

        Assertions.assertEquals("Case03", dispatchBuilder.get(captor.invocation(), proxyConfig).type());
    }

    @Test
    void type_type_11() {
        final var captor = TestUtil.newCaptor(TypeCases.Case03.class);
        captor.proxy().m02();

        Assertions.assertEquals("M02", dispatchBuilder.get(captor.invocation(), proxyConfig).type(),
                "should follow the first annotated");
    }

    @Test
    void ttl_01() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().get();

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).ttl());
    }

    @Test
    void ttl_02() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().get();

        Assertions.assertEquals(Duration.ofDays(1).toMillis(), dispatchBuilder.get(captor.invocation(), new MockProxyConfig() {

            @Override
            public String ttl() {
                return Duration.ofDays(1).toString();
            }

        }).ttl().toMillis());
    }

    @Test
    void ttl_03() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().getTtl01();

        Assertions.assertEquals(Duration.ofDays(1).toMillis(), dispatchBuilder.get(captor.invocation(), new MockProxyConfig() {

            @Override
            public String ttl() {
                return Duration.ofDays(1).toString();
            }

        }).ttl().toMillis());
    }

    @Test
    void ttl_04() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().getTtl02();

        Assertions.assertEquals(Duration.ofSeconds(10).toMillis(), dispatchBuilder.get(captor.invocation(), new MockProxyConfig() {

            @Override
            public String ttl() {
                return Duration.ofDays(1).toString();
            }

        }).ttl().toMillis());
    }

    @Test
    void ttl_05() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().getTtl03();

        Assertions.assertThrows(Exception.class,
                () -> dispatchBuilder.get(captor.invocation(), new MockProxyConfig()));
    }
}
