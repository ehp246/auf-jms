package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.InvocationTargetException;

import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;

/**
 * @author Lei Yang
 *
 */

public sealed interface Invoked permits Completed, Failed {
    BoundInvocable bound();

    public non-sealed interface Completed extends Invoked {
        Object returned();
    }

    public non-sealed interface Failed extends Invoked {
        Throwable thrown();
    }

    static Invoked invoke(final BoundInvocable bound) {
        try {
            final var returned = bound.invocable().method().invoke(bound.invocable().instance(),
                    bound.arguments().toArray());
            return new Completed() {

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
            return new Failed() {

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
            return new Failed() {

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
