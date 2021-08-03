package me.ehp246.aufjms.api.jms;

import java.time.Instant;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Custom version of JMS Message which does not throw.
 * 
 * @author Lei Yang
 *
 */
public interface JmsMsg extends JmsDispatch {
    String id();

    Message message();

    boolean isException();

    long expiration();

    String getInvoking();

    Instant timestamp();

    <T> T property(String name, Class<T> type);

    default String getBodyAsText() {
        try {
            return ((TextMessage) message()).getText();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}