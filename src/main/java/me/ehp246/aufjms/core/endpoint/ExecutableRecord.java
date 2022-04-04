package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.InvocationModel;

/**
 * @author Lei Yang
 *
 */
record ExecutableRecord(Object instance, Method method, InvocationModel invocationModel) implements Executable {
    ExecutableRecord(Object instance, Method method) {
        this(instance, method, InvocationModel.DEFAULT);
    }
}
