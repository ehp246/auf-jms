package me.ehp246.aufjms.api.jms;

import java.time.Instant;
import java.util.Set;

import jakarta.jms.Destination;
import jakarta.jms.TextMessage;

/**
 * The abstraction of an JMS message without the <code>throws</code>.
 * <p>
 * The interface is also meant to enforce read-only policy.
 *
 * @author Lei Yang
 * @since 1.0
 * @see JmsDispatch
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

    boolean redelivered();

    int deliveryCount();

    Instant expiration();

    Instant timestamp();

    String invoking();

    <T> T property(String name, Class<T> type);

    Set<String> propertyNames();

    TextMessage message();
}