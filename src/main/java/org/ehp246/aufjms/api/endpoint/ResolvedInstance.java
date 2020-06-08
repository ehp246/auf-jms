package org.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ResolvedInstance {
	Object getInstance();

	Method getMethod();
	
	default ExecutionModel getExecutionModel() {
		return ExecutionModel.DEFAULT;
	}
}
