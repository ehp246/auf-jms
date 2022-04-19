package me.ehp246.aufjms.core.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Lei Yang
 *
 */
public record ReflectedArgument(Object argument, Parameter parameter, Method method) {

}
