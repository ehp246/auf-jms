package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.InvocationModel;

/**
 * @author Lei Yang
 *
 */
record ExecutableRecord(Object instance, Method method, AutoCloseable closeable, InvocationModel invocationModel)
        implements Executable {
    ExecutableRecord {
        // Instance could be null for static invocation.
        if (method == null) {
            throw new IllegalArgumentException("Method must be specified");
        }

        if (invocationModel == null) {
            throw new IllegalArgumentException("Model must be specified");
        }
    }

    ExecutableRecord(Object instance, Method method) {
        this(instance, method, null, InvocationModel.DEFAULT);
    }
}
