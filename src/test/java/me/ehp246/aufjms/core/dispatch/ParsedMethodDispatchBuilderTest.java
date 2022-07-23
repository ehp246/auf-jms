package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.test.TestUtil;
import me.ehp246.test.TimingExtension;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
class ParsedMethodDispatchBuilderTest {
    private static final ByJmsProxyConfig config = new ByJmsProxyConfig(At.toQueue(UUID.randomUUID().toString()),
            At.toTopic(UUID.randomUUID().toString()), Duration.ofDays(1), Duration.ofSeconds(1),
            UUID.randomUUID().toString());
    private final ProxyMethodParser parser = new ProxyMethodParser(
            new MockEnvironment().withProperty("id", "15df5c8b-adb4-4880-90d9-e370a7a97887")::resolvePlaceholders);

    @Test
    void to_01() {
        final var captor = TestUtil.newCaptor(ToCases.ToCase01.class);

        captor.proxy().m01();

        final var actual = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).to();

        Assertions.assertEquals(config.to(), actual);
    }

    @Test
    void replyTo_01() {
        final var captor = TestUtil.newCaptor(ToCases.ToCase01.class);

        captor.proxy().m01();

        final var actual = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).replyTo();

        Assertions.assertEquals(config.replyTo(), actual);
    }

    @Test
    void type_method_01() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);
        captor.proxy().type01();

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertEquals("Type01", dispatch.type());

        Assertions.assertTrue(dispatch.type() == dispatch.type(),
                "should be the same reference for performance concerns");
    }

    @Test
    void type_arg_01() {
        final var expected = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01(expected);

        final var supplier = parser.parse(captor.invocation().method(), config);

        Assertions.assertEquals(expected, supplier.apply(null, captor.invocation().args().toArray()).type(),
                "should take arg");

        captor.proxy().type01("");

        Assertions.assertEquals("", supplier.apply(null, captor.invocation().args().toArray()).type());

        captor.proxy().type01(null);

        Assertions.assertEquals(null, supplier.apply(null, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_method_02() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type02();

        Assertions.assertEquals("09bf9d41-d65a-4bf3-be39-75a318059c0d", parser
                .parse(captor.invocation().method(), config).apply(null, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_method_03() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type03();

        Assertions.assertEquals("", parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).type());
    }

    @Test
    void correlationId_01() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);
        final var id = Duration.parse("PT100S");

        captor.proxy().m01(id.toString());

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertEquals(id.toString(), dispatch.correlationId());

        Assertions.assertTrue(dispatch.correlationId() == dispatch.correlationId());
    }

    @Test
    void correlationId_02() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m02(null, UUID.randomUUID().toString());

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertEquals(null, dispatch.correlationId(), "should take the first one");
    }

    @Test
    void correlationId_03() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m01();

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertDoesNotThrow(() -> UUID.fromString(dispatch.correlationId()), "should be a UUID");

        Assertions.assertTrue(dispatch.correlationId() == dispatch.correlationId());
    }

    @Test
    void correlationId_04() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m03(Instant.now());

        Assertions.assertThrows(ClassCastException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()));
    }

    @Test
    void ttl_01() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().get();

        Assertions.assertEquals("PT24H", parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).ttl().toString());
    }

    @Test
    void ttl_02() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().get();

        Assertions.assertEquals(Duration.ofDays(1).toMillis(), parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).ttl().toMillis());
    }

    @Test
    void ttl_03() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl01();

        Assertions.assertEquals(Duration.ofMillis(0), parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).ttl(), "should surpress");
    }

    @Test
    void ttl_04() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl02();

        Assertions.assertEquals(Duration.ofSeconds(10).toMillis(), parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).ttl().toMillis());
    }

    @Test
    void ttl_05() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl03();

        Assertions.assertThrows(DateTimeParseException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()));

        Assertions.assertEquals("PT1.1S",
                new ProxyMethodParser(v -> "PT1.1S").parse(captor.invocation().method(), config)
                        .apply(null, captor.invocation().args().toArray()).ttl().toString());
    }

    @Test
    void ttl_06() {
        final var value = new String[1];
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl02();

        final var ttl = new ProxyMethodParser(v -> {
            value[0] = v;
            return "PT1S";
        }).parse(captor.invocation().method(), config).apply(null, captor.invocation().args().toArray()).ttl()
                .toMillis();

        Assertions.assertEquals("PT10S", value[0], "should run it through the resolver");
        Assertions.assertEquals(Duration.parse("PT1S").toMillis(), ttl, "should use the resolved");
    }

    @Test
    void ttl_07() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl03("");

        Assertions.assertThrows(ClassCastException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()));
    }

    @Test
    void ttl_09() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl05(Duration.parse("PT0.1S"));

        Assertions.assertEquals(Duration.parse("PT0.1S").toMillis(), parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).ttl().toMillis());

        captor.proxy().getTtl05(null);

        Assertions.assertEquals(null, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).ttl());
    }

    @Test
    void group_01() {
        final var captor = TestUtil.newCaptor(GroupCases.Case01.class);

        captor.proxy().get();

        Assertions.assertEquals("", parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).groupId());
    }

    @Test
    void group_02() {
        final var captor = TestUtil.newCaptor(GroupCases.Case01.class);

        captor.proxy().get("");

        Assertions.assertEquals("", parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).groupId());
    }

    @Test
    void group_03() {
        final var captor = TestUtil.newCaptor(GroupCases.Case01.class);

        captor.proxy().get(null);

        Assertions.assertEquals(null, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).groupId());
    }

    @Test
    void group_04() {
        final var captor = TestUtil.newCaptor(GroupCases.Case01.class);

        captor.proxy().get2();

        Assertions.assertEquals("15df5c8b-adb4-4880-90d9-e370a7a97887",
                parser.parse(captor.invocation().method(), config).apply(null, captor.invocation().args().toArray())
                        .groupId());
    }

    @Test
    void group_05() {
        final var captor = TestUtil.newCaptor(GroupCases.Case01.class);

        captor.proxy().get3();

        Assertions.assertEquals("id", parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).groupId());
    }

    @Test
    void group_06() {
        final var captor = TestUtil.newCaptor(GroupCases.Case01.class);

        captor.proxy().get(0);

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray())).printStackTrace();
    }

    @Test
    void group_07() {
        final var captor = TestUtil.newCaptor(GroupCases.Case02.class);

        captor.proxy().get("0");

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray())).printStackTrace();
    }

    @Test
    void group_08() {
        final var captor = TestUtil.newCaptor(GroupCases.Case02.class);

        captor.proxy().get(-1);

        Assertions.assertEquals(-1, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).groupSeq());
    }

    @Test
    void group_09() {
        final var captor = TestUtil.newCaptor(GroupCases.Case02.class);

        captor.proxy().get(Integer.valueOf(1));

        Assertions.assertEquals(1, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).groupSeq());
    }

    @Test
    void group_10() {
        final var captor = TestUtil.newCaptor(GroupCases.Case02.class);

        captor.proxy().get((Integer) null);

        Assertions.assertThrows(NullPointerException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).groupSeq()).printStackTrace();
    }

    @Test
    void delay_m01_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();
        final var property = new String[1];
        final var dispatch = new ProxyMethodParser(v -> {
            property[0] = v;
            return "PT0.1S";
        }).parse(captor.invocation().method(), config).apply(null, captor.invocation().args().toArray());

        Assertions.assertEquals(100, dispatch.delay().toMillis());

        Assertions.assertEquals("PT2S", property[0], "should be from the property resolver");
        Assertions.assertTrue(dispatch.delay() == dispatch.delay());
    }

    @Test
    void delay_m01() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertEquals(2000, dispatch.delay().toMillis());
        Assertions.assertTrue(dispatch.delay() == dispatch.delay());
    }

    @Test
    void delay_m01_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);
        final var expected = Duration.ofDays(1);

        captor.proxy().m01(expected);

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertEquals(1, dispatch.delay().toDays(), "should be the argument");
        Assertions.assertTrue(expected == dispatch.delay());
    }

    @Test
    void delay_m01_3() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((Duration) null);

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertEquals(null, dispatch.delay());
        Assertions.assertTrue(dispatch.delay() == dispatch.delay());
    }

    @Test
    void delay_m01_4() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01("");

        Assertions.assertThrows(DateTimeParseException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()));

    }

    @Test
    void delay_m01_5() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01("P90D");

        final var dispatch = parser.parse(captor.invocation().method(), config).apply(null,
                captor.invocation().args().toArray());

        Assertions.assertEquals(7776000000L, dispatch.delay().toMillis());
        Assertions.assertTrue(dispatch.delay() == dispatch.delay());
    }

    @Test
    void delay_m01_6() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01("9");

        Assertions.assertThrows(DateTimeParseException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()));
    }

    @Test
    void delay_m01_7() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01(Instant.now());

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray())).printStackTrace();
        ;
    }

    @Test
    void delay_m01_8() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((String) null);

        Assertions.assertEquals(null, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).delay());
    }

    @Test
    void delay_m03_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03();

        Assertions.assertEquals(Duration.ofMillis(0), parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).delay(), "should suppress");
    }

    @Test
    void delay_m04() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m04(Duration.parse("PT112S"));

        Assertions.assertEquals(Duration.ofSeconds(112), parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).delay(), "should nbe the argument");
    }

    @Test
    void property_m01_11() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key1", "value1"), Map.of("key2", "value2"));

        final var properties = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("value1", properties.get("key1"));
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_12() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("");

        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()), "should require a property name");
    }

    @Test
    void property_m01_21() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(null, Map.of("key2", "value2"));

        final var properties = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_22() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key2", "value1"), Map.of("key2", "value2"));

        final var properties = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"), "should be overwritten by later value");
    }

    @Test
    void property_m01_23() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        final var map = new HashMap<String, Object>();
        map.put("key1", null);

        captor.proxy().m01(map, Map.of("key2", ""));

        final var properties = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(2, properties.keySet().size());
        Assertions.assertEquals(null, properties.get("key1"), "should accept null");
        Assertions.assertEquals("", properties.get("key2"));
    }

    @Test
    void property_m01_31() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, UUID.randomUUID().toString(), Map.of("key2", "value2"));

        final var properties = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(3, properties.keySet().size());

        Assertions.assertEquals("id1", properties.get("ID"));
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_32() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, UUID.randomUUID().toString(), Map.of("ID", "id2"));

        final var properties = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("id2", properties.get("ID"), "should be overwritten by later value");
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
    }

    @Test
    void body_01() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertEquals(null, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_02() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var expected = Map.of("", "");

        captor.proxy().m02(expected);

        Assertions.assertEquals(expected, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_m03_1() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), "");

        Assertions.assertEquals(null, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_m03_2() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), UUID.randomUUID().toString(), null);

        Assertions.assertEquals(null, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_03() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var expected = Instant.now();

        captor.proxy().m04(expected);

        final var actual = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).body();
        final var actualAs = parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).bodyAs();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(TemporalAccessor.class, actualAs.type());
    }

    @Test
    void body_04() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(UUID.randomUUID().toString(), body);

        Assertions.assertEquals(body, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_05() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(body, UUID.randomUUID().toString(), null);

        Assertions.assertEquals(body, parser.parse(captor.invocation().method(), config)
                .apply(null, captor.invocation().args().toArray()).body());
    }
}
