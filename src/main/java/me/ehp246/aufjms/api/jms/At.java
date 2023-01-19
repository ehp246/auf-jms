package me.ehp246.aufjms.api.jms;

import jakarta.jms.Destination;

/**
 * A named {@linkplain Destination}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface At permits AtTopic, AtQueue {
    String name();

    static AtQueue toQueue(final String name) {
        return new Queue(name);
    }

    static AtTopic toTopic(final String name) {
        return new Topic(name);
    }
}