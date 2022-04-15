package me.ehp246.aufjms.util;

import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;

public class MockTextMessage implements TextMessage {
	private String messageId = UUID.randomUUID().toString();
	private Destination replyTo;
	private String correlId;
	private String type;
	private String text;
    private Destination destination = (Queue) () -> "mock";
	private long timestamp = Instant.now().toEpochMilli();
	private Map<String, String> property = new HashMap<>();

	public MockTextMessage() {
		super();
	}

	public MockTextMessage(String type) {
		super();
		this.type = type;
	}

	@Override
	public String getJMSMessageID() throws JMSException {
		return messageId;
	}

	@Override
	public void setJMSMessageID(String id) throws JMSException {
		this.messageId = id;
	}

	@Override
	public long getJMSTimestamp() throws JMSException {
		return timestamp;
	}

	@Override
	public void setJMSTimestamp(long timestamp) throws JMSException {
		this.timestamp = timestamp;
	}

	@Override
	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		return null;
	}

	@Override
	public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
	}

	@Override
	public void setJMSCorrelationID(String correlationID) throws JMSException {
		this.correlId = correlationID;
	}

	@Override
	public String getJMSCorrelationID() throws JMSException {
		return this.correlId;
	}

	@Override
	public Destination getJMSReplyTo() throws JMSException {
		return this.replyTo;
	}

	@Override
	public void setJMSReplyTo(Destination replyTo) throws JMSException {
		this.replyTo = replyTo;
	}

	@Override
	public Destination getJMSDestination() throws JMSException {
		return this.destination;
	}

	@Override
	public void setJMSDestination(Destination destination) throws JMSException {
		this.destination = destination;
	}

	@Override
	public int getJMSDeliveryMode() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
	}

	@Override
	public boolean getJMSRedelivered() throws JMSException {
		return false;
	}

	@Override
	public void setJMSRedelivered(boolean redelivered) throws JMSException {
	}

	@Override
	public String getJMSType() throws JMSException {
		return this.type;
	}

	@Override
	public void setJMSType(String type) throws JMSException {
		this.type = type;
	}

	public MockTextMessage withJMSType(String type) {
		this.type = type;
		return this;
	}

	@Override
	public long getJMSExpiration() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSExpiration(long expiration) throws JMSException {
	}

	@Override
	public int getJMSPriority() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSPriority(int priority) throws JMSException {
	}

	@Override
	public void clearProperties() throws JMSException {
		this.property.clear();
	}

	@Override
	public boolean propertyExists(String name) throws JMSException {
		return this.property.containsKey(name);
	}

	@Override
	public boolean getBooleanProperty(String name) throws JMSException {
		return Boolean.parseBoolean(this.property.get(name));
	}

	@Override
	public byte getByteProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public short getShortProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public int getIntProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public long getLongProperty(String name) throws JMSException {
		return Long.parseLong(this.property.get(name));
	}

	@Override
	public float getFloatProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public double getDoubleProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public String getStringProperty(String name) throws JMSException {
		return property.get(name);
	}

	@Override
	public Object getObjectProperty(String name) throws JMSException {
		return null;
	}

	@Override
	public Enumeration<String> getPropertyNames() throws JMSException {
		return Collections.enumeration(this.property.keySet());
	}

	@Override
	public void setBooleanProperty(String name, boolean value) throws JMSException {
	}

	@Override
	public void setByteProperty(String name, byte value) throws JMSException {
	}

	@Override
	public void setShortProperty(String name, short value) throws JMSException {
	}

	@Override
	public void setIntProperty(String name, int value) throws JMSException {

	}

	@Override
	public void setLongProperty(String name, long value) throws JMSException {
	}

	@Override
	public void setFloatProperty(String name, float value) throws JMSException {
	}

	@Override
	public void setDoubleProperty(String name, double value) throws JMSException {
	}

	@Override
	public void setStringProperty(String name, String value) throws JMSException {
		this.property.put(name, value);
	}

	public MockTextMessage withStringProperty(String name, String value) {
		this.property.put(name, value);
		return this;
	}

	@Override
	public void setObjectProperty(String name, Object value) throws JMSException {
	}

	@Override
	public void acknowledge() throws JMSException {
	}

	@Override
	public void clearBody() throws JMSException {
	}

	@Override
	public void setText(String string) throws JMSException {
		this.text = string;
	}

	public MockTextMessage withText(String string) {
		this.text = string;
		return this;
	}

	@Override
	public String getText() throws JMSException {
		return this.text;
	}

    @Override
    public long getJMSDeliveryTime() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setJMSDeliveryTime(long deliveryTime) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public <T> T getBody(Class<T> c) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBodyAssignableTo(Class c) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

}
