package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;

import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 *
 */
public interface ByJmsProxyConfig {
    At to();

    At replyTo();

    Duration ttl();

    Duration delay();

    String connectionFactory();
}