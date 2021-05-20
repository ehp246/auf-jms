package me.ehp246.aufjms.api.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Custom version of JMS Message which does not throw.
 * 
 * @author Lei Yang
 *
 */
public interface Received extends Msg {
    Message message();

    boolean isException();

    String getInvoking();

    default String getBodyAsText() {
        try {
            return ((TextMessage) message()).getText();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}