package me.ehp246.aufjms.integration.dispatch.listener;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record DispatchRecord(JmsDispatch dispatch, JmsMsg msg, Exception e) {

}
