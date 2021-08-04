package me.ehp246.aufjms.api.jms;

import javax.jms.Message;

/**
 * The abstraction of turning an {@link Invocation} to a {@link JmsDispatch} so
 * it can be sent out as {@link Message}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocationDispatchProvider {
    JmsDispatch get(ByJmsProxyConfig config, Invocation invocation);
}
