package me.ehp246.aufjms.core.endpoint;

import java.util.Collections;
import java.util.List;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record BoundInvocableRecord(Invocable invocable, List<Object> arguments, JmsMsg msg)
        implements BoundInvocable {
    BoundInvocableRecord {
        if (invocable == null) {
            throw new IllegalArgumentException("Target must be specified");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must be specified");
        }

        arguments = Collections.unmodifiableList(arguments);
    }

    BoundInvocableRecord(Invocable invocable, JmsMsg msg) {
        this(invocable, List.of(), msg);
    }
}
