package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import me.ehp246.aufjms.api.dispatch.ByJmsConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.test.TestUtil;
import me.ehp246.test.TestUtil.InvocationCaptor;
import me.ehp246.test.TimingExtension;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
@EnabledIfSystemProperty(named = "me.ehp246.aufjms.perfTest", matches = "true")
class DispatchBuilderPerfTest {
    private final static int COUNT = 10_000_000;
    private final static At at = At.toQueue("d");
    private static final ByJmsConfig BYJMS_CONFIG = new ByJmsConfig(at, at, Duration.ofHours(12), Duration.ofSeconds(2),
            "");

    private static final DefaultInvocationDispatchBuilder dispatchBuilder = new DefaultInvocationDispatchBuilder(
            String::toString);
    private static final InvocationCaptor<PerfCase> captor = TestUtil.newCaptor(PerfCase.class);

    @Test
    void perf_01_1() {
        captor.proxy().m01();

        final var invocation = captor.invocation();
        final var target = invocation.target();
        final var method = invocation.method();
        final var args = invocation.args().toArray();

        IntStream.range(0, COUNT).forEach(i -> {
            dispatchBuilder.get(target, method, args, BYJMS_CONFIG);
        });
    }

    @Test
    void perf_01_2() {
        captor.proxy().m01();

        final var invocation = captor.invocation();
        final var args = invocation.args().toArray();
        final var method = invocation.method();

        final var parsedMethod = ParsedMethodSupplier.parse(method);

        IntStream.range(0, COUNT).forEach(i -> {
            parsedMethod.apply(BYJMS_CONFIG, args);
        });
    }

    @Test
    void perf_02_1() {
        captor.proxy().m02(Map.of());

        final var invocation = captor.invocation();
        final var target = invocation.target();
        final var method = invocation.method();
        final var args = invocation.args().toArray();

        IntStream.range(0, COUNT).forEach(i -> {
            dispatchBuilder.get(target, method, args, BYJMS_CONFIG);
        });
    }

    @Test
    void perf_02_2() {
        captor.proxy().m02(Map.of());

        final var invocation = captor.invocation();
        final var args = invocation.args().toArray();
        final var method = invocation.method();

        final var parsedMethod = ParsedMethodSupplier.parse(method);

        IntStream.range(0, COUNT).forEach(i -> {
            parsedMethod.apply(BYJMS_CONFIG, args);
        });
    }


    @Test
    void perf_03_1() {
        captor.proxy().m03(UUID.randomUUID().toString());

        final var invocation = captor.invocation();
        final var target = invocation.target();
        final var method = invocation.method();
        final var args = invocation.args().toArray();

        IntStream.range(0, COUNT).forEach(i -> {
            dispatchBuilder.get(target, method, args, BYJMS_CONFIG);
        });
    }

    @Test
    void perf_03_2() {
        captor.proxy().m03(UUID.randomUUID().toString());

        final var invocation = captor.invocation();
        final var args = invocation.args().toArray();
        final var method = invocation.method();

        final var parsedMethod = ParsedMethodSupplier.parse(method);

        IntStream.range(0, COUNT).forEach(i -> {
            parsedMethod.apply(BYJMS_CONFIG, args);
        });
    }

    @Test
    void perf_04_1() {
        final var linked = new LinkedList<>(List.of(UUID.randomUUID().toString()));

        captor.proxy().m04(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                Map.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()), linked);

        final var invocation = captor.invocation();
        final var target = invocation.target();
        final var method = invocation.method();
        final var args = invocation.args().toArray();

        IntStream.range(0, COUNT).forEach(i -> {
            dispatchBuilder.get(target, method, args, BYJMS_CONFIG);
        });
    }

    @Test
    void perf_04_2() {
        final var linked = new LinkedList<>(List.of(UUID.randomUUID().toString()));

        captor.proxy().m04(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                Map.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()), linked);

        final var invocation = captor.invocation();
        final var args = invocation.args().toArray();
        final var method = invocation.method();

        final var parsedMethod = ParsedMethodSupplier.parse(method);

        IntStream.range(0, COUNT).forEach(i -> {
            parsedMethod.apply(BYJMS_CONFIG, args);
        });
    }
}
