package me.ehp246.aufjms.core.reflection;

import java.util.concurrent.Callable;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface CatchingInvocation {
    InvocationOutcome<Object> invoke();

    static <T> InvocationOutcome<T> invoke(final Callable<T> callable) {
        try {
            return InvocationOutcome.returned(callable.call());
        } catch (final Exception e) {
            return InvocationOutcome.thrown(e);
        }
    }
}
