package me.ehp246.aufjms.api.endpoint;

import javax.jms.JMSContext;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface MsgContext {
    JmsMsg msg();

    JMSContext jmsContext();
}