package me.ehp246.test.embedded.dispatch.listener;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record DispatchRecord(JmsDispatch dispatch, JmsMsg msg, Exception e) {

}
