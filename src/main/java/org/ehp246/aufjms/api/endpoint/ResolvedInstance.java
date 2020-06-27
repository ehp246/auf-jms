package org.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ResolvedInstance {
	Object getInstance();

	Method getMethod();

	default InvocationModel getExecutionModel() {
		return InvocationModel.DEFAULT;
	}

	default Consumer<ExecutedInstance> postExecution() {
		return null;
	}
}
