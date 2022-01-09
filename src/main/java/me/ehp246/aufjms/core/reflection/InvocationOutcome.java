package me.ehp246.aufjms.core.reflection;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final class InvocationOutcome<T> {
    private final T returned;
    private final Throwable thrown;
    private final boolean hasReturned;

    private InvocationOutcome(final T returned, final Throwable thrown, final boolean hasReturned) {
        super();
        this.returned = returned;
        this.hasReturned = hasReturned;
        this.thrown = thrown;
    }

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


    public T getReturned() {
        return returned;
    }

    public Throwable getThrown() {
        return thrown;
    }

    public boolean hasReturned() {
        return hasReturned;
    }

    public boolean hasThrown() {
        return !hasReturned;
    }

    public Object outcomeValue() {
        return hasReturned() ? getReturned() : getThrown();
    }

    public Optional<T> optionalReturned() {
        return hasReturned() ? Optional.of(returned) : Optional.empty();
    }
}