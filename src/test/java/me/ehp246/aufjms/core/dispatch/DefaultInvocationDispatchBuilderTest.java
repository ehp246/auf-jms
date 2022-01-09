package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.DispatchConfig;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.core.jms.AtQueueRecord;
import me.ehp246.aufjms.util.MockProxyConfig;
import me.ehp246.aufjms.util.TestUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocationDispatchBuilderTest {
    private final static AtDestination at = new AtQueueRecord("");

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

    private DefaultInvocationDispatchBuilder dispatchBuilder = new DefaultInvocationDispatchBuilder(String::toString);

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

        captor.proxy().type01_II("");

        Assertions.assertEquals("", dispatchBuilder.get(captor.invocation(), proxyConfig).type(),
                "should supress the default");
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

        Assertions.assertEquals(TypeCases.TYPE_I, dispatchBuilder.get(captor.invocation(), proxyConfig).type(),
                "should use the default");
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

        Assertions.assertEquals(Duration.ofDays(1).toMillis(),
                dispatchBuilder.get(captor.invocation(), new MockProxyConfig() {

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

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), new MockProxyConfig() {

            @Override
            public String ttl() {
                return Duration.ofDays(1).toString();
            }

        }).ttl(), "should surpress");
    }

    @Test
    void ttl_04() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);
        captor.proxy().getTtl02();

        Assertions.assertEquals(Duration.ofSeconds(10).toMillis(),
                dispatchBuilder.get(captor.invocation(), new MockProxyConfig() {

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

        Assertions.assertThrows(Exception.class, () -> dispatchBuilder.get(captor.invocation(), proxyConfig));
    }

    @Test
    void ttl_06() {
        final var value = new String[1];
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl02();

        final var ttl = new DefaultInvocationDispatchBuilder(v -> {
            value[0] = v;
            return "PT1S";
        }).get(captor.invocation(), proxyConfig).ttl().toMillis();

        Assertions.assertEquals("PT10S", value[0], "should run it through the resolver");
        Assertions.assertEquals(Duration.parse("PT1S").toMillis(), ttl, "should use the resolved");
    }

    @Test
    void ttl_07() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl03("PT0.1S");

        Assertions.assertEquals(Duration.parse("PT0.1S").toMillis(),
                dispatchBuilder.get(captor.invocation(), proxyConfig).ttl().toMillis());

        captor.proxy().getTtl03("");

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).ttl(),
                "should suppress other annotations");

        captor.proxy().getTtl03(null);

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).ttl(),
                "should suppress other annotations");
    }

    @Test
    void ttl_08() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl04("PT0.1S");

        Assertions.assertEquals(Duration.parse("PT0.1S").toMillis(),
                dispatchBuilder.get(captor.invocation(), proxyConfig).ttl().toMillis());

        captor.proxy().getTtl04("");

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).ttl(),
                "should supress the default");

        captor.proxy().getTtl04(null);

        Assertions.assertEquals(Duration.parse("PT1S").toMillis(),
                dispatchBuilder.get(captor.invocation(), proxyConfig).ttl().toMillis(),
                "should use it for the default");
    }

    @Test
    void ttl_09() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl05(Duration.parse("PT0.1S"));

        Assertions.assertEquals(Duration.parse("PT0.1S").toMillis(),
                dispatchBuilder.get(captor.invocation(), proxyConfig).ttl().toMillis());

        captor.proxy().getTtl05(null);

        Assertions.assertEquals(Duration.parse("PT1S").toMillis(),
                dispatchBuilder.get(captor.invocation(), proxyConfig).ttl().toMillis(),
                "should use it for the default");
    }

    @Test
    void body_01() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertEquals(0, dispatchBuilder.get(captor.invocation(), proxyConfig).bodyValues().size());
    }

    @Test
    void body_02() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(body);

        final var bodyValues = dispatchBuilder.get(captor.invocation(), proxyConfig).bodyValues();

        Assertions.assertEquals(1, bodyValues.size());
        Assertions.assertEquals(body, bodyValues.get(0));
    }

    @Test
    void body_m03_1() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), "");

        Assertions.assertEquals(0, dispatchBuilder.get(captor.invocation(), proxyConfig).bodyValues().size());
    }

    @Test
    void body_m03_2() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "");

        Assertions.assertEquals(0, dispatchBuilder.get(captor.invocation(), proxyConfig).bodyValues().size());
    }

    @Test
    void body_04() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(UUID.randomUUID().toString(), body);

        final var bodyValues = dispatchBuilder.get(captor.invocation(), proxyConfig).bodyValues();

        Assertions.assertEquals(1, bodyValues.size());
        Assertions.assertEquals(body, bodyValues.get(0));
    }

    @Test
    void body_05() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(body, UUID.randomUUID().toString(), null);

        final var bodyValues = dispatchBuilder.get(captor.invocation(), proxyConfig).bodyValues();

        Assertions.assertEquals(1, bodyValues.size());
        Assertions.assertEquals(body, bodyValues.get(0));
    }

    @Test
    void property_m01_11() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key1", "value1"), Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation(), proxyConfig).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("value1", properties.get("key1"));
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_12() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("");

        Assertions.assertThrows(RuntimeException.class, () -> dispatchBuilder.get(captor.invocation(), proxyConfig),
                "should require a property name");
    }

    @Test
    void property_m01_21() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(null, Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation(), proxyConfig).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_22() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key2", "value1"), Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation(), proxyConfig).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"), "should be overwritten by later value");
    }

    @Test
    void property_m01_23() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        final var map = new HashMap<String, Object>();
        map.put("key1", null);

        captor.proxy().m01(map, Map.of("key2", ""));

        final var properties = dispatchBuilder.get(captor.invocation(), proxyConfig).properties();

        Assertions.assertEquals(2, properties.keySet().size());
        Assertions.assertEquals(null, properties.get("key1"), "should accept null");
        Assertions.assertEquals("", properties.get("key2"));
    }

    @Test
    void property_m01_31() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation(), proxyConfig).properties();

        Assertions.assertEquals(3, properties.keySet().size());

        Assertions.assertEquals("id1", properties.get("ID"));
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_32() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, Map.of("ID", "id2"));

        final var properties = dispatchBuilder.get(captor.invocation(), proxyConfig).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("id2", properties.get("ID"), "should be overwritten by later value");
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
    }

    @Test
    void delay_m01() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertEquals(2000, dispatchBuilder.get(captor.invocation(), proxyConfig).delay().toMillis());
    }

    @Test
    void delay_m01_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();

        final var property = new String[1];
        Assertions.assertEquals(100, new DefaultInvocationDispatchBuilder(v -> {
            property[0] = v;
            return "PT0.1S";
        }).get(captor.invocation(), proxyConfig).delay().toMillis());

        Assertions.assertEquals("PT2S", property[0]);
    }

    @Test
    void delay_m01_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01(Duration.ofDays(1));

        Assertions.assertEquals(1, dispatchBuilder.get(captor.invocation(), proxyConfig).delay().toDays());
    }

    @Test
    void delay_m01_3() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((Duration) null);

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).delay());
    }

    @Test
    void delay_m01_4() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01(Duration.ofMillis(1).toString());

        Assertions.assertEquals(1, dispatchBuilder.get(captor.invocation(), proxyConfig).delay().toMillis());
    }

    @Test
    void delay_m01_5() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((String) null);

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).delay());
    }

    @Test
    void delay_m02_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m02(null);

        Assertions.assertEquals(2, dispatchBuilder.get(captor.invocation(), proxyConfig).delay().toSeconds(),
                "should use the default");
    }

    @Test
    void delay_m02_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m02("PT100S");

        Assertions.assertEquals(100, dispatchBuilder.get(captor.invocation(), proxyConfig).delay().toSeconds());
    }

    @Test
    void correlationId_01() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);
        final var id = Duration.parse("PT100S");

        captor.proxy().m01(id);

        Assertions.assertEquals(id.toString(), dispatchBuilder.get(captor.invocation(), proxyConfig).correlationId());
    }

    @Test
    void correlationId_02() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m02(null, UUID.randomUUID().toString());

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation(), proxyConfig).correlationId(),
                "should take the first one");
    }
}
