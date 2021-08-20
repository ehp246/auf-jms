package me.ehp246.aufjms.api.jms;

import java.time.Instant;

import javax.jms.Destination;
import javax.jms.TextMessage;

/**
 * Custom version of JMS Message which does not throw.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface JmsMsg {
    String id();

    Destination destination();

    String type();

    String correlationId();

    String text();

    Destination replyTo();

    String groupId();

    Integer groupSeq();

    long expiration();

    Instant timestamp();

    String invoking();

    <T> T property(String name, Class<T> type);

    TextMessage msg();
}