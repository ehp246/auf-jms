package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 * @since 1.0
 */
public record Topic(String name) implements AtTopic {
    public Topic {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Un-supported name: '" + name + "'");
        }
    }
}
