package me.ehp246.aufjms.core.dispatch;

/**
 * @author Lei Yang
 *
 */
public record EnableByJmsConfig(String ttl) {
    public EnableByJmsConfig() {
        this("");
    }
}
