package me.ehp246.aufjms.api.spi;

import me.ehp246.aufjms.api.jms.BodyOf;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, BodyOf<?> valueInfo);
}
