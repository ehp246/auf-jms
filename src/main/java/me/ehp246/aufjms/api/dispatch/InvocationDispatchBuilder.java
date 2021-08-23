package me.ehp246.aufjms.api.dispatch;

import javax.jms.Message;

import me.ehp246.aufjms.api.jms.Invocation;

/**
 * The abstraction of turning an {@link Invocation} to a {@link JmsDispatch} so
 * it can be sent out as {@link Message}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocationDispatchBuilder {
    JmsDispatch get(Invocation invocation, DispatchConfig config);
}
