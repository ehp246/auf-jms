package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.InvocationTargetException;

import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface BoundInvocable {
    Invocable invocable();

    JmsMsg msg();

    /**
     * Resolved argument values ready for invocation. Should never be
     * <code>null</code>.
     */
    Object[] arguments();

    default Invoked invoke() {
        try {
            final var returned = this.invocable().method().invoke(this.invocable().instance(), this.arguments());
            return new Completed() {

                @Override
                public BoundInvocable bound() {
                    return BoundInvocable.this;
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
                    return BoundInvocable.this;
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
                    return BoundInvocable.this;
                }

                @Override
                public Throwable thrown() {
                    return e2;
                }
            };
        }
    }
}
