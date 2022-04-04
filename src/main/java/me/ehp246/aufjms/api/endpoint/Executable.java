package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.function.Consumer;

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

    default Consumer<ExecutedInstance> executionConsumer() {
        return null;
    }
}
