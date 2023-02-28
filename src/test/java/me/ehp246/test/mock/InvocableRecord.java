package me.ehp246.test.mock;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocationModel;

/**
 * @author Lei Yang
 *
 */
public record InvocableRecord(Object instance, Method method, AutoCloseable closeable, InvocationModel invocationModel)
        implements Invocable {
    public InvocableRecord {
        // Instance could be null for static invocation.
        if (method == null) {
            throw new IllegalArgumentException("Method must be specified");
        }

        if (invocationModel == null) {
            throw new IllegalArgumentException("Model must be specified");
        }
    }

    public InvocableRecord(final Object instance, final Method method) {
        this(instance, method, null, InvocationModel.DEFAULT);
    }

    @Override
    public void close() throws Exception {
    }
}
