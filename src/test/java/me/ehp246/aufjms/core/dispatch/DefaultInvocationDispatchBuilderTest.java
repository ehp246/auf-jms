package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.times;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.dispatch.ByJmsConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocationDispatchBuilderTest {
    private final static At at = At.toQueue("d");

    private static final ByJmsConfig BYJMS_CONFIG = new ByJmsConfig(at, at, Duration.ofHours(12), Duration.ofSeconds(2), "");

    private final static ByJmsConfig proxyConfig = new ByJmsConfig(at, at);

    private final PropertyResolver resolver = Mockito.mock(PropertyResolver.class);

    private final DefaultInvocationDispatchBuilder dispatchBuilder = new DefaultInvocationDispatchBuilder(
            String::toString);



    @Test
    void body_01() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).body());
    }

    @Test
    void body_02() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var expected = Map.of("", "");

        captor.proxy().m02(expected);

        Assertions.assertEquals(expected, dispatchBuilder.get(captor.invocation().target(),
                captor.invocation().method(), captor.invocation().args().toArray(), proxyConfig).body());
    }

    @Test
    void body_m03_1() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), "");

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).body());
    }

    @Test
    void body_m03_2() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        captor.proxy().m03(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "");

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).body());
    }

    @Test
    void body_03() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var expected = Instant.now();

        captor.proxy().m04(expected);

        final var actual = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).body();
        final var actualAs = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).bodyAs();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(TemporalAccessor.class, actualAs.type());
    }

    @Test
    void body_04() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(UUID.randomUUID().toString(), body);

        Assertions.assertEquals(body, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).body());
    }

    @Test
    void body_05() {
        final var captor = TestUtil.newCaptor(BodyCases.Case01.class);

        final var body = Map.of("", "");

        captor.proxy().m02(body, UUID.randomUUID().toString(), null);

        Assertions.assertEquals(body, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).body());
    }

    @Test
    void property_m01_11() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key1", "value1"), Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("value1", properties.get("key1"));
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_12() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("");

        Assertions.assertThrows(RuntimeException.class,
                () -> dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                        captor.invocation().args().toArray(), proxyConfig),
                "should require a property name");
    }

    @Test
    void property_m01_21() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(null, Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_22() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01(Map.of("key2", "value1"), Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).properties();

        Assertions.assertEquals(1, properties.keySet().size());

        Assertions.assertEquals("value2", properties.get("key2"), "should be overwritten by later value");
    }

    @Test
    void property_m01_23() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        final var map = new HashMap<String, Object>();
        map.put("key1", null);

        captor.proxy().m01(map, Map.of("key2", ""));

        final var properties = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).properties();

        Assertions.assertEquals(2, properties.keySet().size());
        Assertions.assertEquals(null, properties.get("key1"), "should accept null");
        Assertions.assertEquals("", properties.get("key2"));
    }

    @Test
    void property_m01_31() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, Map.of("key2", "value2"));

        final var properties = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).properties();

        Assertions.assertEquals(3, properties.keySet().size());

        Assertions.assertEquals("id1", properties.get("ID"));
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
        Assertions.assertEquals("value2", properties.get("key2"));
    }

    @Test
    void property_m01_32() {
        final var captor = TestUtil.newCaptor(PropertyCases.Case01.class);

        captor.proxy().m01("id1", 123, Map.of("ID", "id2"));

        final var properties = dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).properties();

        Assertions.assertEquals(2, properties.keySet().size());

        Assertions.assertEquals("id2", properties.get("ID"), "should be overwritten by later value");
        Assertions.assertEquals(123, ((Integer) properties.get("SEQ")).intValue());
    }

    @Test
    void delay_m01() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertEquals(2000, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).delay().toMillis());
    }

    @Test
    void delay_m01_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01();

        final var property = new String[1];
        Assertions.assertEquals(100, new DefaultInvocationDispatchBuilder(v -> {
            property[0] = v;
            return "PT0.1S";
        }).get(captor.invocation().target(), captor.invocation().method(), captor.invocation().args().toArray(),
                proxyConfig).delay().toMillis());

        Assertions.assertEquals("PT2S", property[0], "should be from the property resolver");
    }

    @Test
    void delay_m01_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01(Duration.ofDays(1));

        Assertions.assertEquals(1, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).delay().toDays());
    }

    @Test
    void delay_m01_3() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((Duration) null);

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).delay());
    }

    @Test
    void delay_m01_4() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01(Duration.ofMillis(1).toString());

        Assertions.assertEquals(1, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).delay().toMillis());
    }

    @Test
    void delay_m01_5() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m01((String) null);

        Assertions.assertEquals(null, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).delay());
    }

    @Test
    void delay_m02_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m02(null);

        Assertions.assertEquals(2,
                dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                        captor.invocation().args().toArray(), proxyConfig).delay().toSeconds(),
                "should use the default");
    }

    @Test
    void delay_m02_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m02("PT100S");

        Assertions.assertEquals(100, dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                captor.invocation().args().toArray(), proxyConfig).delay().toSeconds());
    }

    @Test
    void delay_m03_1() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03();

        Assertions.assertEquals(null,
                dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                        captor.invocation().args().toArray(),
                        new ByJmsConfig(at, at, Duration.ofHours(1), Duration.ofSeconds(2), "")).delay(),
                "should suppress");
    }

    @Test
    void delay_m03_2() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03("");

        Assertions.assertEquals(null,
                dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                        captor.invocation().args().toArray(),
                        new ByJmsConfig(at, at, Duration.ofHours(2), Duration.ofSeconds(2), "")).delay(),
                "should suppress");
    }

    @Test
    void delay_m03_3() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03(null);

        Assertions.assertEquals(Duration.ofSeconds(2),
                dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                        captor.invocation().args().toArray(),
                        BYJMS_CONFIG).delay(),
                "should not suppress");
    }

    @Test
    void delay_m03_4() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m03("${delay}");

        Assertions.assertThrows(DateTimeParseException.class,
                () -> new DefaultInvocationDispatchBuilder(resolver)
                        .get(captor.invocation().target(), captor.invocation().method(),
                        captor.invocation().args().toArray(),
                        BYJMS_CONFIG).delay(),
                "should not suppress");

        Mockito.verify(resolver, times(0)).resolve(Mockito.anyString());
    }

    @Test
    void delay_m04() {
        final var captor = TestUtil.newCaptor(DelayCases.Case01.class);

        captor.proxy().m04(Duration.parse("PT112S"));

        Assertions.assertEquals(Duration.ofSeconds(112),
                dispatchBuilder.get(captor.invocation().target(), captor.invocation().method(),
                        captor.invocation().args().toArray(),
                        BYJMS_CONFIG).delay(),
                "should not suppress");
    }
}
