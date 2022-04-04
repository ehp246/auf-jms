package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface Executable {
    Object instance();

    Method method();

    default InvocationModel invocationModel() {
        return InvocationModel.DEFAULT;
    }
}
