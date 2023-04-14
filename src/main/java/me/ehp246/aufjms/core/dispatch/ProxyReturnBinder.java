package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProxyReturnBinder {
    Object apply(JmsMsg responseMsg);
}
