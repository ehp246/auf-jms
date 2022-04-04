package me.ehp246.aufjms.core.reflection;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final record InvocationOutcome<T> (T returned, Throwable thrown, boolean hasReturned) {
    public static <T> InvocationOutcome<T> returned(final T returned) {
        return new InvocationOutcome<T>(returned, null, true);
    }

    public static <T> InvocationOutcome<T> thrown(final Throwable thrown) {
        return new InvocationOutcome<T>(null, thrown, false);
    }

    public static <T> InvocationOutcome<T> invoke(final Callable<T> callable) {
        try {
            return InvocationOutcome.returned(callable.call());
        } catch (final Exception e) {
            return InvocationOutcome.thrown(e);
        }
    }

    public boolean hasThrown() {
        return !hasReturned;
    }

    public Object outcomeValue() {
        return hasReturned() ? returned() : thrown();
    }

    public Optional<T> optionalReturned() {
        return hasReturned() ? Optional.of(returned) : Optional.empty();
    }
}