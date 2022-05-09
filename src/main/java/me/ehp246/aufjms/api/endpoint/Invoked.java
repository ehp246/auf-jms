package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Lei Yang
 *
 */

public sealed interface Invoked permits CompletedInvocation, FailedInvocation {
    BoundInvocable bound();

    static Invoked invoke(final BoundInvocable bound) {
        try {
            final var returned = bound.invocable().method().invoke(bound.invocable().instance(),
                    bound.arguments().toArray());
            return new CompletedInvocation() {

                @Override
                public BoundInvocable bound() {
                    return bound;
                }

                @Override
                public Object returned() {
                    return returned;
                }
            };
        } catch (InvocationTargetException e1) {
            return new FailedInvocation() {

                @Override
                public BoundInvocable bound() {
                    return bound;
                }

                @Override
                public Throwable thrown() {
                    return e1.getCause();
                }
            };
        } catch (Exception e2) {
            return new FailedInvocation() {

                @Override
                public BoundInvocable bound() {
                    return bound;
                }

                @Override
                public Throwable thrown() {
                    return e2;
                }
            };
        }
    }
}
