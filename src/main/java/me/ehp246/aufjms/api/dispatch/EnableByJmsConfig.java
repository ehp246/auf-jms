package me.ehp246.aufjms.api.dispatch;

import java.util.List;

/**
 * @author Lei Yang
 * @since 1.0
 */
public record EnableByJmsConfig(List<Class<?>> scan, String ttl, List<String> dispatchFns) {
    public EnableByJmsConfig() {
        this(List.of(), "", List.of());
    }
}
