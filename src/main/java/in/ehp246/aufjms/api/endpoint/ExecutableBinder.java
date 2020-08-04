package in.ehp246.aufjms.api.endpoint;

import in.ehp246.aufjms.core.reflection.ReflectingInvocation;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ExecutableBinder {
	ReflectingInvocation bind(ResolvedExecutable resolved, InvocationContext invocationContext);
}
