package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface BoundInvocable {
    Invocable invocable();

    JmsMsg msg();

    /**
     * Resolved argument values ready for invocation. Should never be
     * <code>null</code>.
     */
    Object[] arguments();
}
