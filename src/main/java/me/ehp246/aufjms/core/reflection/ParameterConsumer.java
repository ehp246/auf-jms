package me.ehp246.aufjms.core.reflection;

import java.lang.reflect.Parameter;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ParameterConsumer {
    void accept(Parameter parameter, int index);
}
