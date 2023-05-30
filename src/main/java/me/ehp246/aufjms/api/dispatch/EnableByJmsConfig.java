package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 * @since 1.0
 */
public record EnableByJmsConfig(List<Class<?>> scan, Duration ttl, Duration delay, List<String> dispatchFns,
        At requestReplyAt) {
    public EnableByJmsConfig {
        scan = Collections.unmodifiableList(scan);
        dispatchFns = Collections.unmodifiableList(dispatchFns);
    }

    public EnableByJmsConfig() {
        this(List.of(), null, null, List.of(), null);
    }
}
