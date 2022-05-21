package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;

import org.mockito.Mockito;

import me.ehp246.aufjms.api.dispatch.ByJmsConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.spi.PropertyResolver;

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


}
