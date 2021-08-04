package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface ByJmsProxyConfig {
    String destination();

    long ttl();

    String connection();

    String replyTo();
}
