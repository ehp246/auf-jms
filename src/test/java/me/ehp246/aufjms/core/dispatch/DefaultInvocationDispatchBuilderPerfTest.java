package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.stream.IntStream;

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
class DefaultInvocationDispatchBuilderPerfTest {
    private final static int COUNT = 5_000_000;
    private final static At at = At.toQueue("d");
    private static final ByJmsConfig BYJMS_CONFIG = new ByJmsConfig(at, at, Duration.ofHours(12), Duration.ofSeconds(2),
            "");

    private final DefaultInvocationDispatchBuilder dispatchBuilder = new DefaultInvocationDispatchBuilder(
            String::toString);
    private static final InvocationCaptor<PerfCase> captor = TestUtil.newCaptor(PerfCase.class);

    @Test
    void perf_01() {
        captor.proxy().m01();

        final var invocation = captor.invocation();
        final var target = invocation.target();
        final var method = invocation.method();
        final var args = invocation.args().toArray();

        IntStream.range(0, COUNT).forEach(i -> {
            dispatchBuilder.get(target, method, args, BYJMS_CONFIG);
        });
    }
}
