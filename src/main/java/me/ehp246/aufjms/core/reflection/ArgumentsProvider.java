package me.ehp246.aufjms.core.reflection;

import java.lang.reflect.Method;

@FunctionalInterface
public interface ArgumentsProvider {
    Object[] provideFor(Method method);
}
