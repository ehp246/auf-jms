package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.core.reflection.ReflectingInvocation;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ExecutableBinder {
    ReflectingInvocation bind(ResolvedExecutable resolved, InvocationContext invocationContext);
}
