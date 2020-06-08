package org.ehp246.aufjms.core.reflection;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface CatchingInvoke {
	static <T> InvocationOutcome<T> invoke(Callable<T> callable) {
		try {
			return InvocationOutcome.returned(callable.call());
		} catch(Exception e) {
			return InvocationOutcome.thrown(e);
		}
	}
	/**
	 * Should never throw.
	 * @return
	 */
	<T> InvocationOutcome<T> invoke();
}
