package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 *
 */
public interface ByJmsProxyConfig {
    String destination();

    long ttl();

    String connection();

    String replyTo();
}
