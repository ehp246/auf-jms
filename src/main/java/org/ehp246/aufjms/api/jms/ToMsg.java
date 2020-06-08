package org.ehp246.aufjms.api.jms;

import java.time.Instant;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Utility to un-pack a JMS message.
 * 
 * @author Lei Yang
 *
 */
public class ToMsg implements Msg {
	private Destination replyTo;
	private String correlId;
	private String type;
	private Message message;
	private long expiration;
	private long ttl;
	private Destination destination;
	private String groupId;
	private Integer groupSeq;
	private String actionThrew;
	private String traceId;
	private String spanId;
	private Instant timestamp;
	private Object body;

	private ToMsg(Message msg) {
		super();
		this.message = msg;
	}

	public static Msg wrap(Message message)  {
		if (!(message instanceof TextMessage)) {
			throw new RuntimeException("Un-supported message type: " + message.getClass().getName());
		}
		
		return new ToMsg((TextMessage) message).perform();
	}

	private ToMsg perform() {
		try {
			this.type = message.getJMSType();
			this.replyTo = message.getJMSReplyTo();
			this.correlId = message.getJMSCorrelationID();
			this.expiration = message.getJMSExpiration();
			this.destination = message.getJMSDestination();
			this.groupId = message.getStringProperty(MsgPropertyName.GroupId);
			this.groupSeq = message.getIntProperty(MsgPropertyName.GroupSeq);
			this.actionThrew = message.getStringProperty(MsgPropertyName.ServerThrown);
			this.ttl = this.getLongProperty(message, MsgPropertyName.TTL, 0);
			this.traceId = message.getStringProperty(MsgPropertyName.TraceId);
			this.spanId = message.getStringProperty(MsgPropertyName.SpanId);
			this.timestamp = Instant.ofEpochMilli(message.getJMSTimestamp());
			
			if (message instanceof TextMessage) {
				body = message.getBody(String.class);
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	private long getLongProperty(Message message, String name, long def) {
		try {
			return message.getLongProperty(name);
		} catch (Exception e) {
			// Ignored
		}
		return def;
	}
	
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public Destination getReplyTo() {
		return this.replyTo;
	}

	@Override
	public String getCorrelationId() {
		return this.correlId;
	}

	@Override
	public Message getMessage() {
		return this.message;
	}

	@Override
	public String getGroupId() {
		return this.groupId;
	}

	@Override
	public Integer getGroupSeq() {
		return this.groupSeq;
	}

	@Override
	public String getThrown() {
		return this.actionThrew;
	}

	@Override
	public String getTraceId() {
		return this.traceId;
	}

	@Override
	public String getSpanId() {
		return this.spanId;
	}

	@Override
	public long getExpiration() {
		return this.expiration;
	}

	@Override
	public Destination getDestination() {
		return this.destination;
	}

	@Override
	public long getTtl() {
		return this.ttl;
	}

	@Override
	public Instant getTimestamp() {
		return timestamp;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getProperty(String name, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBody(Class<T> type) {
		return (T)body;
	}
}
