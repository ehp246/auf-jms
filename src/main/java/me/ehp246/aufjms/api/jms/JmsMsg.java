package me.ehp246.aufjms.api.jms;

import java.time.Instant;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.TextMessage;

/**
 * Custom version of JMS Message which does not throw.
 * <p>
 * The interface is also meant to enforce read-only policy.
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

    int groupSeq();

    int deliveryCount();

    Instant expiration();

    Instant timestamp();

    String invoking();

    <T> T property(String name, Class<T> type);

    Set<String> propertyNames();

    TextMessage message();
}