package me.ehp246.aufjms.api.dispatch;

import java.util.function.Supplier;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface BodySupplier extends Supplier<String> {
}
