package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.times;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class ParsedMethodSupplierTest {
    private static final ByJmsProxyConfig config = new ByJmsProxyHandler(At.toQueue(UUID.randomUUID().toString()),
            At.toTopic(UUID.randomUUID().toString()), Duration.ofDays(1), Duration.ofSeconds(1),
            UUID.randomUUID().toString());

    @Test
    void to_01() {
        final var captor = TestUtil.newCaptor(ToCases.ToCase01.class);

        captor.proxy().m01();

        final var actual = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).to();

        Assertions.assertEquals(config.to(), actual);
    }

    @Test
    void replyTo_01() {
        final var captor = TestUtil.newCaptor(ToCases.ToCase01.class);

        captor.proxy().m01();

        final var actual = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).replyTo();

        Assertions.assertEquals(config.replyTo(), actual);
    }

    @Test
    void type_method_01() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);
        captor.proxy().type01();

        Assertions.assertEquals("Type01", ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_arg_01() {
        final var expected = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01(expected);

        final var supplier = ParsedMethodSupplier.parse(captor.invocation().method());

        Assertions.assertEquals(expected, supplier.apply(config, captor.invocation().args().toArray()).type(),
                "should take arg");

        captor.proxy().type01("");

        Assertions.assertEquals("", supplier.apply(config, captor.invocation().args().toArray()).type());

        captor.proxy().type01(null);

        Assertions.assertEquals(null, supplier.apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_method_02() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type02();

        Assertions.assertEquals("09bf9d41-d65a-4bf3-be39-75a318059c0d", ParsedMethodSupplier
                .parse(captor.invocation().method()).apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_method_03() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type03();

        Assertions.assertEquals("", ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_01() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(argType);

        Assertions.assertEquals(argType, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_02() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(null);

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_03() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type02();

        Assertions.assertEquals("6f7779af-8c3e-4684-8a12-537415281b89", ParsedMethodSupplier
                .parse(captor.invocation().method()).apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_04() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type03();

        Assertions.assertEquals("09bf9d41-d65a-4bf3-be39-75a318059c0d", ParsedMethodSupplier
                .parse(captor.invocation().method()).apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_05() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type04();

        Assertions.assertEquals("", ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).type());
    }

    @Test
    void correlationId_01() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);
        final var id = Duration.parse("PT100S");

        captor.proxy().m01(id);

        Assertions.assertEquals(id.toString(), ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).correlationId());
    }

    @Test
    void correlationId_02() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m02(null, UUID.randomUUID().toString());

        Assertions.assertEquals(null,
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).correlationId(),
                "should take the first one");
    }

    @Test
    void correlationId_03() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m01();

        Assertions
                .assertDoesNotThrow(
                        () -> UUID.fromString(ParsedMethodSupplier.parse(captor.invocation().method())
                                .apply(config, captor.invocation().args().toArray()).correlationId()),
                        "should be a UUID");
    }

    @Test
    void ttl_01() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().get();

        Assertions.assertEquals("PT24H", ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).ttl().toString());
    }

    @Test
    void ttl_02() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().get();

        Assertions.assertEquals(Duration.ofDays(1).toMillis(), ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).ttl().toMillis());
    }

    @Test
    void ttl_03() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl01();

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).ttl(), "should surpress");
    }

    @Test
    void ttl_04() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl02();

        Assertions.assertEquals(Duration.ofSeconds(10).toMillis(),
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).ttl().toMillis());
    }

    @Test
    void ttl_05() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl03();

        Assertions.assertThrows(DateTimeParseException.class, () -> ParsedMethodSupplier
                .parse(captor.invocation().method()).apply(config, captor.invocation().args().toArray()));

        Assertions.assertEquals("PT1.1S", ParsedMethodSupplier.parse(captor.invocation().method(), v -> "PT1.1S")
                .apply(config, captor.invocation().args().toArray()).ttl().toString());
    }

    @Test
    void ttl_06() {
        final var value = new String[1];
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl02();

        final var ttl = ParsedMethodSupplier.parse(captor.invocation().method(), v -> {
            value[0] = v;
            return "PT1S";
        }).apply(config, captor.invocation().args().toArray()).ttl().toMillis();

        Assertions.assertEquals("PT10S", value[0], "should run it through the resolver");
        Assertions.assertEquals(Duration.parse("PT1S").toMillis(), ttl, "should use the resolved");
    }

    @Test
    void ttl_07() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl03("PT0.1S");

        Assertions.assertEquals(Duration.parse("PT0.1S").toMillis(),
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).ttl().toMillis());

        captor.proxy().getTtl03("");

        Assertions.assertEquals(null,
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).ttl(),
                "should suppress other annotations");

        captor.proxy().getTtl03(null);

        Assertions.assertEquals(null,
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).ttl(),
                "should suppress other annotations");
    }

    @Test
    void ttl_08() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl04("PT0.1S");

        Assertions.assertEquals(Duration.parse("PT0.1S").toMillis(),
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).ttl().toMillis());

        captor.proxy().getTtl04("");

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).ttl(), "should ignore the annotated");

        captor.proxy().getTtl04(null);

        Assertions.assertEquals(null,
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).ttl(),
                "should ignore the annotated value");
    }

    @Test
    void ttl_09() {
        final var captor = TestUtil.newCaptor(TtlCases.Case01.class);

        captor.proxy().getTtl05(Duration.parse("PT0.1S"));

        Assertions.assertEquals(Duration.parse("PT0.1S").toMillis(),
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).ttl().toMillis());

        captor.proxy().getTtl05(null);

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).ttl());
    }

    @Test
    void delay_m01_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();

        final var property = new String[1];
        Assertions.assertEquals(100, ParsedMethodSupplier.parse(captor.invocation().method(), v -> {
            property[0] = v;
            return "PT0.1S";
        }).apply(config, captor.invocation().args().toArray()).delay().toMillis());

        Assertions.assertEquals("PT2S", property[0], "should be from the property resolver");
    }

    @Test
    void delay_m01() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertEquals(2000, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay().toMillis());
    }

    @Test
    void delay_m01_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01(Duration.ofDays(1));

        Assertions.assertEquals(1,
                ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config, captor.invocation().args().toArray()).delay().toDays(),
                "should be the argument");
    }

    @Test
    void delay_m01_3() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((Duration) null);

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay());
    }

    @Test
    void delay_m01_4() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01(Duration.ofMillis(1).toString());

        Assertions.assertEquals(1, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay().toMillis());
    }

    @Test
    void delay_m01_5() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((String) null);

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay());
    }

    @Test
    void delay_m02_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m02(null);

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay(), "should use the argument");
    }

    @Test
    void delay_m02_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m02("PT100S");

        Assertions.assertEquals(100, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay().toSeconds());
    }

    @Test
    void delay_m03_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03();

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay(), "should suppress");
    }

    @Test
    void delay_m03_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03("");

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay());
    }

    @Test
    void delay_m03_3() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03(null);

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay());
    }

    @Test
    void delay_m03_4() {
        final PropertyResolver resolver = Mockito.mock(PropertyResolver.class);

        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03("${delay}");

        Assertions.assertThrows(DateTimeParseException.class,
                () -> ParsedMethodSupplier.parse(captor.invocation().method(), resolver)
                        .apply(config, captor.invocation().args().toArray()).delay());
        
        Mockito.verify(resolver, times(0)).resolve(Mockito.anyString());
    }

    @Test
    void delay_m04() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m04(Duration.parse("PT112S"));

        Assertions.assertEquals(Duration.ofSeconds(112), ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).delay(), "should nbe the argument");
    }

    @Test
    void property_m01_11() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key1", "value1"), Map.of("key2", "value2"));

        final var properties = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("value1", properties.get("key1"));
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_12() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("");

        Assertions.assertThrows(
                IllegalArgumentException.class, () -> ParsedMethodSupplier.parse(captor.invocation().method())
                        .apply(config,
                        captor.invocation().args().toArray()),
                "should require a property name");
    }

    @Test
    void property_m01_21() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(null, Map.of("key2", "value2"));

        final var properties = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_22() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key2", "value1"), Map.of("key2", "value2"));

        final var properties = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"), "should be overwritten by later value");
    }

    @Test
    void property_m01_23() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        final var map = new HashMap<String, Object>();
        map.put("key1", null);

        captor.proxy().m01(map, Map.of("key2", ""));

        final var properties = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(2, properties.keySet().size());
        Assertions.assertEquals(null, properties.get("key1"), "should accept null");
        Assertions.assertEquals("", properties.get("key2"));
    }

    @Test
    void property_m01_31() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, Map.of("key2", "value2"));

        final var properties = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(3, properties.keySet().size());

        Assertions.assertEquals("id1", properties.get("ID"));
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_32() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, Map.of("ID", "id2"));

        final var properties = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("id2", properties.get("ID"), "should be overwritten by later value");
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
    }

    @Test
    void body_01() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_02() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var expected = Map.of("", "");

        captor.proxy().m02(expected);

        Assertions.assertEquals(expected, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_m03_1() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), "");

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_m03_2() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "");

        Assertions.assertEquals(null, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_03() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var expected = Instant.now();

        captor.proxy().m04(expected);

        final var actual = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).body();
        final var actualAs = ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).bodyAs();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(TemporalAccessor.class, actualAs.type());
    }

    @Test
    void body_04() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(UUID.randomUUID().toString(), body);

        Assertions.assertEquals(body, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).body());
    }

    @Test
    void body_05() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(body, UUID.randomUUID().toString(), null);

        Assertions.assertEquals(body, ParsedMethodSupplier.parse(captor.invocation().method())
                .apply(config, captor.invocation().args().toArray()).body());
    }
}
