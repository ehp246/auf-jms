package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.At;
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
}
