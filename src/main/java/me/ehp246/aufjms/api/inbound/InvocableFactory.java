package me.ehp246.aufjms.api.inbound;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Internal abstraction that creates an {@linkplain Invocable} given a
 * {@linkplain JmsMsg}.
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableFactory {
    Invocable get(JmsMsg msg);
}
