package org.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Abstraction of a message action by type to be registered in the registry.
 * 
 * @author Lei Yang
 *
 */
public interface TypeActionDefinition {
	Set<String> getType();

	Class<?> getActionClass();

	Map<String, Method> getPerformMethods();

	InstanceScope getScope();

	ExecutionModel getExecutionModel();
}
