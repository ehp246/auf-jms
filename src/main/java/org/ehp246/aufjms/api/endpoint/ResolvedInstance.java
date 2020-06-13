package org.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

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

	default List<Consumer<ExecutedInstance>> postExecution() {
		return null;
	}
}
