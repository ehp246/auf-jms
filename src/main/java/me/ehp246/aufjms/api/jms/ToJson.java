package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, BodyOf<?> valueInfo);

    default String apply(final Object value) {
        return this.apply(value, value == null ? null : new BodyOf<>(value.getClass()));
    }
}
