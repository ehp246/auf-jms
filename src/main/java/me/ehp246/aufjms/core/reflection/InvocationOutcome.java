package me.ehp246.aufjms.core.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final record InvocationOutcome(Object returned, Throwable thrown, boolean hasReturned) {
    public static InvocationOutcome returned(final Object returned) {
        return new InvocationOutcome(returned, null, true);
    }

    public static InvocationOutcome thrown(final Throwable thrown) {
        return new InvocationOutcome(null, thrown, false);
    }

    public static InvocationOutcome invoke(final BoundInvocable bound) {
        try {
            return InvocationOutcome.returned(bound.method().invoke(bound.instance(), bound.arguments().toArray()));
        } catch (InvocationTargetException e1) {
            return InvocationOutcome.thrown(e1.getCause());
        } catch (Exception e2) {
            return InvocationOutcome.thrown(e2);
        }
    }

    public boolean hasThrown() {
        return !hasReturned;
    }

    public Object outcomeValue() {
        return hasReturned() ? returned() : thrown();
    }

    public Optional<?> optionalReturned() {
        return hasReturned() ? Optional.of(returned) : Optional.empty();
    }
}