package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
sealed interface ProxyReturnBinder {
}

@FunctionalInterface
non-sealed interface LocalReturnBinder extends ProxyReturnBinder {
    Object apply(JmsDispatch dispatch);
}

@FunctionalInterface
non-sealed interface RemoteReturnBinder extends ProxyReturnBinder {
    Object apply(JmsDispatch dispatch, JmsMsg returnMsg);
}
