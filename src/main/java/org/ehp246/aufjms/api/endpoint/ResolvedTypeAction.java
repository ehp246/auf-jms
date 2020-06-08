package org.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ResolvedTypeAction {
	String getType();

	Class<?> getActionClass();

	Method getPerformMethod();

	InstanceScope getScope();
	
	ExecutionModel getExecutionModel();
}
