package me.ehp246.aufjms.api.spi;

/**
 * The abstraction of the functionality to resolve Spring property placeholders
 * and SpEL expressions.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ExpressionResolver {
    String resolve(String expression);
}
