package org.ehp246.aufjms.util;

import java.time.Instant;
import java.util.concurrent.Callable;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.api.jms.MsgPropertyName;

/**
 * Utility to un-pack a JMS message.
 * 
 * @author Lei Yang
 *
 */
public class ToMsg {
	private ToMsg() {
		super();
	}

	public static Msg from(final Message message) {
		if (!(message instanceof TextMessage)) {
			throw new RuntimeException("Un-supported message type: " + message.getClass().getName());
		}

		return new Msg() {

			@Override
			public String getId() {
				return invoke(message::getJMSMessageID);
			}

			@Override
			public String getType() {
				return invoke(message::getJMSType);
			}

			@Override
			public String getCorrelationId() {
				return invoke(message::getJMSCorrelationID);
			}

			@Override
			public Destination getReplyTo() {
				return invoke(message::getJMSReplyTo);
			}

			@Override
			public String getGroupId() {
				return invoke(() -> message.getStringProperty(MsgPropertyName.GroupId));
			}

			@Override
			public Integer getGroupSeq() {
				return invoke(() -> message.getIntProperty(MsgPropertyName.GroupSeq));
			}

			@Override
			public String getThrown() {
				return invoke(() -> message.getStringProperty(MsgPropertyName.ServerThrown));
			}

			@Override
			public String getTraceId() {
				return invoke(() -> message.getStringProperty(MsgPropertyName.TraceId));
			}

			@Override
			public String getSpanId() {
				return invoke(() -> message.getStringProperty(MsgPropertyName.SpanId));
			}

			@Override
			public long getExpiration() {
				return invoke(message::getJMSExpiration);
			}

			@Override
			public Destination getDestination() {
				return invoke(message::getJMSDestination);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T> T getProperty(String name, Class<T> type) {
				if (type == String.class) {
					return (T) invoke(() -> message.getStringProperty(name));
				}
				if (type == int.class) {
					return (T) invoke(() -> message.getIntProperty(name));
				}
				if (type == long.class) {
					return (T) invoke(() -> message.getLongProperty(name));
				}
				if (type == boolean.class) {
					return (T) invoke(() -> message.getBooleanProperty(name));
				}

				throw new RuntimeException("Un-supported property type " + type.getTypeName());
			}

			@Override
			public long getTtl() {
				return invoke(() -> message.getIntProperty(MsgPropertyName.TTL));
			}

			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochMilli(invoke(message::getJMSTimestamp));
			}

			@Override
			public Message getMessage() {
				return message;
			}

		};
	}

	public static <V> V invoke(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
