package me.ehp246.aufjms.api.jms;

import java.time.Instant;

import javax.jms.TextMessage;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;

/**
 * Custom version of JMS Message which does not throw.
 * 
 * @author Lei Yang
 *
 */
public interface JmsMsg extends JmsDispatch {
    String id();

    String text();

    long expiration();

    Instant timestamp();

    String invoking();

    <T> T property(String name, Class<T> type);

    TextMessage message();
}