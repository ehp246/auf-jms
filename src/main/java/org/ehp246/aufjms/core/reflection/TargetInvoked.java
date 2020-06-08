package org.ehp246.aufjms.core.reflection;

import java.lang.reflect.Method;

public interface TargetInvoked {
	Object getTarget();

	Method getMethod();

	Object[] getArguments();

	InvocationOutcome getResult();
}
