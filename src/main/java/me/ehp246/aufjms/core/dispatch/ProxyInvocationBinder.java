package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.jms.JmsDispatch;

/**
 * @author Lei Yang
 * @since 2.0
 */
@FunctionalInterface
public interface ProxyInvocationBinder {
    JmsDispatch apply(final Object target, final Object[] args);
}
