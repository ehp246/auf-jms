package org.ehp246.aufjms.api.endpoint;

import org.ehp246.aufjms.core.reflection.ReflectingInvocation;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ActionInvocationBinder {
	ReflectingInvocation bind(ResolvedInstance resolved, ActionInvocationContext invocationContext);
}
