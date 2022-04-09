package me.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * A named {@linkplain Destination}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface To permits ToTopic, ToQueue {
    String name();

    static ToQueue toQueue(String name) {
        return new Queue(name);
    }

    static ToTopic toTopic(String name) {
        return new Topic(name);
    }
}