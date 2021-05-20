package me.ehp246.aufjms.api.jms;

import java.time.Instant;
import java.util.List;

import javax.jms.Destination;

/**
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

    String groupId();

    Integer groupSeq();

    long expiration();

    <T> T property(String name, Class<T> type);

    long ttl();

    Instant timestamp();
}
