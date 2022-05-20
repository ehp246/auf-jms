package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.At;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class ParsedMethodSupplierTest {
    private static final ByJmsProxyHandler handler = new ByJmsProxyHandler(At.toQueue(UUID.randomUUID().toString()),
            At.toTopic(UUID.randomUUID().toString()), Duration.ofDays(1), Duration.ofSeconds(1),
            UUID.randomUUID().toString());

    @Test
    void to_01() {
        final var captor = TestUtil.newCaptor(ToCases.ToCase01.class);

        captor.proxy().m01();

        final var actual = ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).to();

        Assertions.assertEquals(handler.to(), actual);
    }

    @Test
    void replyTo_01() {
        final var captor = TestUtil.newCaptor(ToCases.ToCase01.class);

        captor.proxy().m01();

        final var actual = ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).replyTo();

        Assertions.assertEquals(handler.replyTo(), actual);
    }

    @Test
    void type_method_01() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);
        captor.proxy().type01();

        Assertions.assertEquals("Type01", ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_arg_01() {
        final var expected = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type01(expected);

        final var supplier = ParsedMethodSupplier.get(captor.invocation().method());

        Assertions.assertEquals(expected, supplier.apply(handler, captor.invocation().args().toArray()).type(),
                "should take arg");

        captor.proxy().type01("");

        Assertions.assertEquals("", supplier.apply(handler, captor.invocation().args().toArray()).type());

        captor.proxy().type01(null);

        Assertions.assertEquals(null, supplier.apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_method_02() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type02();

        Assertions.assertEquals("09bf9d41-d65a-4bf3-be39-75a318059c0d", ParsedMethodSupplier
                .get(captor.invocation().method()).apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_method_03() {
        final var captor = TestUtil.newCaptor(TypeCases.Case01.class);

        captor.proxy().type03();

        Assertions.assertEquals("", ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_01() {
        final var argType = UUID.randomUUID().toString();

        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(argType);

        Assertions.assertEquals(argType, ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_02() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type01(null);

        Assertions.assertEquals(null, ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_03() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type02();

        Assertions.assertEquals("6f7779af-8c3e-4684-8a12-537415281b89", ParsedMethodSupplier
                .get(captor.invocation().method()).apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_04() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type03();

        Assertions.assertEquals("09bf9d41-d65a-4bf3-be39-75a318059c0d", ParsedMethodSupplier
                .get(captor.invocation().method()).apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void type_02_05() {
        final var captor = TestUtil.newCaptor(TypeCases.Case02.class);

        captor.proxy().type04();

        Assertions.assertEquals("", ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).type());
    }

    @Test
    void correlationId_01() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);
        final var id = Duration.parse("PT100S");

        captor.proxy().m01(id);

        Assertions.assertEquals(id.toString(), ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).correlationId());
    }

    @Test
    void correlationId_02() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m02(null, UUID.randomUUID().toString());

        Assertions.assertEquals(null,
                ParsedMethodSupplier.get(captor.invocation().method())
                        .apply(handler, captor.invocation().args().toArray()).correlationId(),
                "should take the first one");
    }

    @Test
    void correlationId_03() {
        final var captor = TestUtil.newCaptor(CorrelationIdCases.Case01.class);

        captor.proxy().m01();

        Assertions.assertDoesNotThrow(() -> UUID.fromString(ParsedMethodSupplier.get(captor.invocation().method())
                .apply(handler, captor.invocation().args().toArray()).correlationId()), "should be a UUID");
    }
}
