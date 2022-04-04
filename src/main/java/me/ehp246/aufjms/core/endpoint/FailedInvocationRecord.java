package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.FailedMsg;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record FailedMsgRecord(JmsMsg msg, Throwable thrown) implements FailedMsg {

}
