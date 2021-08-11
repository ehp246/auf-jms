package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.core.reflection.ReflectingInvocation;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ExecutableBinder {
    ReflectingInvocation bind(Executable resolved, InvocationContext invocationContext);
}
