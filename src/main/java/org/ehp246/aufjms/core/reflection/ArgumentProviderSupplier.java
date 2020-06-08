package org.ehp246.aufjms.core.reflection;

@FunctionalInterface
public interface ArgumentProviderSupplier<T> {
	ArgumentsProvider get(T source);
}
