package org.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Abstraction of a message executable by type to be registered in the registry.
 *
 * @author Lei Yang
 *
 */
public interface InvokingDefinition {
	String getMsgType();

	Class<?> getInstanceType();

	Map<String, Method> getMethods();

	InstanceScope getInstanceScope();

	InvocationMode getInvocationMode();
}
