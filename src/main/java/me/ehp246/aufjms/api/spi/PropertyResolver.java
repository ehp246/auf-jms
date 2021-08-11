package me.ehp246.aufjms.api.spi;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface PropertyResolver {
    String resolve(String text);
}
