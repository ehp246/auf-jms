package me.ehp246.aufjms.core.reflection;

import java.util.function.Supplier;

import me.ehp246.aufjms.core.reflection.ValueSupplier.IndexSupplier;
import me.ehp246.aufjms.core.reflection.ValueSupplier.SimpleSupplier;

/**
 * @author Lei Yang
 *
 */
public sealed interface ValueSupplier permits IndexSupplier, SimpleSupplier {
    @FunctionalInterface
    non-sealed interface IndexSupplier extends ValueSupplier {
        int get();
    }

    @FunctionalInterface
    non-sealed interface SimpleSupplier extends ValueSupplier, Supplier<Object> {
        @Override
        String get();
    }
}
