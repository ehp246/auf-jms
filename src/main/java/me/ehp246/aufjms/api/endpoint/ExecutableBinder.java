package me.ehp246.aufjms.api.endpoint;

import java.util.function.Supplier;

import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ExecutableBinder {
    Supplier<InvocationOutcome<?>> bind(Executable resolved, MsgContext invocationContext);
}
