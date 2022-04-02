package me.ehp246.aufjms.integration.endpoint.failedmsg;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
record DeadMsg(JmsMsg msg, Exception ex) {

}
