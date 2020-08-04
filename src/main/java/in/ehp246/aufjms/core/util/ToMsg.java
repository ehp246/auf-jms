package in.ehp246.aufjms.core.util;

import java.time.Instant;
import java.util.concurrent.Callable;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

import in.ehp246.aufjms.api.jms.Msg;
import in.ehp246.aufjms.api.jms.MsgPropertyName;

/**
 * Utility to un-pack a JMS message.
 * 
 * @author Lei Yang
 *
 */
public class ToMsg {
	private static final class MsgImplementation implements Msg {
		private final Message message;

		private MsgImplementation(Message message) {
			this.message = message;
		}

		@Override
		public String getId() {
			return invoke(message::getJMSMessageID);
		}

		@Override
		public String getType() {
			return invoke(message::getJMSType);
		}

		@Override
		public String getInvoking() {
			return invoke(() -> message.getStringProperty(MsgPropertyName.Invoking));
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
		public boolean isException() {
			return invoke(() -> message.getBooleanProperty(MsgPropertyName.ServerThrown));
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
	}

	private ToMsg() {
		super();
	}

	public static Msg from(final Message message) {
		if (!(message instanceof TextMessage)) {
			throw new RuntimeException("Un-supported message type: " + message.getClass().getName());
		}

		return new MsgImplementation(message);
	}

	private static <V> V invoke(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
