package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.core.dispatch.ValueSupplier.IndexSupplier;
import me.ehp246.aufjms.core.dispatch.ValueSupplier.StaticSupplier;

/**
 * @author Lei Yang
 *
 */
public sealed interface ValueSupplier permits IndexSupplier, StaticSupplier {
    @FunctionalInterface
    non-sealed interface IndexSupplier extends ValueSupplier {
        int get();
    }

    @FunctionalInterface
    non-sealed interface StaticSupplier extends ValueSupplier {
        Object get();
    }
}
