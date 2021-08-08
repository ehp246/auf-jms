package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface Executable {
    Object getInstance();

    Method getMethod();

    default InvocationModel getInvocationModel() {
        return InvocationModel.DEFAULT;
    }

    default Consumer<ExecutedInstance> postExecution() {
        return null;
    }
}
