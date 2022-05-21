package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
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

}
