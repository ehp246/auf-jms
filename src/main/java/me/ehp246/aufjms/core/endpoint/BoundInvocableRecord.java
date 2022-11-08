package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.Invocable;
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
}
