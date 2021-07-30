package me.ehp246.aufjms.api.jms;

import java.time.Instant;
import java.util.List;

import javax.jms.Destination;

/**
 * The abstraction of an out-going JMS message.
 * 
 * @author Lei Yang
 *
 */
public interface Msg {
    String id();

    String type();

    Destination destination();

    List<?> bodyValues();

    String correlationId();

    Destination replyTo();

    long expiration();

    long ttl();

    Instant timestamp();

    <T> T property(String name, Class<T> type);

    String groupId();

    int groupSeq();
}
