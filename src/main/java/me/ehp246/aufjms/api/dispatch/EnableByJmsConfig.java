package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig.ReplyAt;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 * @since 1.0
 */
public record EnableByJmsConfig(List<Class<?>> scan, Duration ttl, Duration delay, List<String> dispatchFns,
        ReplyAt replyAt) {
    public EnableByJmsConfig {
        scan = Collections.unmodifiableList(scan);
        dispatchFns = Collections.unmodifiableList(dispatchFns);
    }

    public EnableByJmsConfig() {
        this(List.of(), null, null, List.of(), null);
    }

    public record ReplyAt(String value, DestinationType type) {
    }
}
