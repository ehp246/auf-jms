package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.InvocationTargetException;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.Invoked;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record BoundInvocableRecord(Invocable invocable, Object[] arguments, JmsMsg msg)
        implements BoundInvocable {
    BoundInvocableRecord {
        if (invocable == null) {
            throw new IllegalArgumentException("Target must be specified");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must be specified");
        }
    }

    BoundInvocableRecord(Invocable invocable, JmsMsg msg) {
        this(invocable, new Object[] {}, msg);
    }

    @Override
    public Invoked invoke() {
        try {
            final var returned = this.invocable().method().invoke(this.invocable().instance(), this.arguments());
            return new Completed() {

                @Override
                public BoundInvocable bound() {
                    return BoundInvocableRecord.this;
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
                    return BoundInvocableRecord.this;
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
                    return BoundInvocableRecord.this;
                }

                @Override
                public Throwable thrown() {
                    return e2;
                }
            };
        }
    }
}
