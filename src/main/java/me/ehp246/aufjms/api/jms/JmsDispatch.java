package me.ehp246.aufjms.api.jms;

import java.util.List;

import javax.jms.Destination;

/**
 * The abstraction of an out-going JMS message.
 * 
 * @author Lei Yang
 *
 */
public interface JmsDispatch {
    Destination destination();

    default String type() {
        return null;
    }

    default String correlationId() {
        return null;
    }

    default List<?> bodyValues() {
        return null;
    }

    default Destination replyTo() {
        return null;
    }

    default Long ttl() {
        return null;
    }

    default <T> T property(String name, Class<T> type) {
        return null;
    }

    default String groupId() {
        return null;
    }

    default Integer groupSeq() {
        return null;
    }
}
