package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, BodyOf<?> valueInfo);
}
