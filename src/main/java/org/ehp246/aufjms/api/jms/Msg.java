package org.ehp246.aufjms.api.jms;

import java.time.Instant;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Custom version of JMS Message which does not throw.
 * 
 * @author Lei Yang
 *
 */
public interface Msg {
	String getId();

	String getType();

	String getCorrelationId();

	Destination getReplyTo();

	String getGroupId();

	Integer getGroupSeq();

	String getThrown();

	String getTraceId();

	String getSpanId();

	long getExpiration();

	Destination getDestination();

	<T> T getProperty(String name, Class<T> type);

	long getTtl();

	Instant getTimestamp();

	Message getMessage();

	default String getBodyAsText() {
		try {
			return ((TextMessage) getMessage()).getText();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}