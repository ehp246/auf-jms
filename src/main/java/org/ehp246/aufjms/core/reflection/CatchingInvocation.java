package org.ehp246.aufjms.core.reflection;

import java.util.concurrent.Callable;

public interface CatchingInvocation {
	static <T> InvocationOutcome<T> invoke(Callable<T> callable) {
		try {
			return InvocationOutcome.returned(callable.call());
		} catch (Exception e) {
			return InvocationOutcome.thrown(e);
		}
	}

	static InvocationOutcome<Void> invoke(Runnable runnable) {
		try {
			runnable.run();
			return InvocationOutcome.returned(null);
		} catch (Exception e) {
			return InvocationOutcome.thrown(e);
		}
	}
}
