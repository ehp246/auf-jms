package org.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 *
 * @author Lei Yang
 *
 */
public interface ResolvedExecutable {
	Object getInstance();

	Method getMethod();

	default InvocationMode getInvocationMode() {
		return InvocationMode.DEFAULT;
	}

	default Consumer<ExecutedInstance> postExecution() {
		return null;
	}
}
