package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 * @since 1.0
 */
record Queue(String name) implements AtQueue {
    Queue {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Un-supported name: '" + name + "'");
        }
    }
}
