package me.ehp246.aufjms.api.dispatch;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public interface DispatchListener {
    void onDispatch(JmsMsg msg);
}
