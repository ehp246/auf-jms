package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.Invocable;

/**
 * @author Lei Yang
 *
 */
record BoundInvocableRecord(Invocable executable, List<Object> arguments)
        implements BoundInvocable {
    BoundInvocableRecord {
        if (executable == null) {
            throw new IllegalArgumentException("Target must be specified");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must be specified");
        }

        arguments = Collections.unmodifiableList(arguments);
    }

    BoundInvocableRecord(Invocable executable) {
        this(executable, List.of());
    }

    @Override
    public Object instance() {
        return executable.instance();
    }

    @Override
    public Method method() {
        return executable.method();
    }
}
