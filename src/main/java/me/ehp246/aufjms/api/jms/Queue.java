package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 * @since 1.0
 */
public record Queue(String name) implements AtQueue {
    public Queue {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Un-supported name: '" + name + "'");
        }
    }
}
