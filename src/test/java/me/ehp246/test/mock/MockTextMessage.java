package me.ehp246.test.mock;

import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

public class MockTextMessage implements TextMessage {
	private String messageId = UUID.randomUUID().toString();
    private Destination replyTo = (Queue) () -> "replyMock";
    private String correlId = UUID.randomUUID().toString();
	private String type;
	private String text;
    private Destination destination = (Queue) () -> "mock";
	private long timestamp = Instant.now().toEpochMilli();
	private final Map<String, String> property = new HashMap<>();

	public MockTextMessage() {
		super();
	}

	public MockTextMessage(final String type) {
		super();
		this.type = type;
	}

	@Override
	public String getJMSMessageID() throws JMSException {
		return messageId;
	}

	@Override
	public void setJMSMessageID(final String id) throws JMSException {
		this.messageId = id;
	}

	@Override
	public long getJMSTimestamp() throws JMSException {
		return timestamp;
	}

	@Override
	public void setJMSTimestamp(final long timestamp) throws JMSException {
		this.timestamp = timestamp;
	}

	@Override
	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		return null;
	}

	@Override
	public void setJMSCorrelationIDAsBytes(final byte[] correlationID) throws JMSException {
	}

	@Override
	public void setJMSCorrelationID(final String correlationID) throws JMSException {
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
	public void setJMSReplyTo(final Destination replyTo) throws JMSException {
		this.replyTo = replyTo;
	}

	@Override
	public Destination getJMSDestination() throws JMSException {
		return this.destination;
	}

	@Override
	public void setJMSDestination(final Destination destination) throws JMSException {
		this.destination = destination;
	}

	@Override
	public int getJMSDeliveryMode() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSDeliveryMode(final int deliveryMode) throws JMSException {
	}

	@Override
	public boolean getJMSRedelivered() throws JMSException {
		return false;
	}

	@Override
	public void setJMSRedelivered(final boolean redelivered) throws JMSException {
	}

	@Override
	public String getJMSType() throws JMSException {
		return this.type;
	}

	@Override
	public void setJMSType(final String type) throws JMSException {
		this.type = type;
	}

	public MockTextMessage withJMSType(final String type) {
		this.type = type;
		return this;
	}

	@Override
	public long getJMSExpiration() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSExpiration(final long expiration) throws JMSException {
	}

	@Override
	public int getJMSPriority() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSPriority(final int priority) throws JMSException {
	}

	@Override
	public void clearProperties() throws JMSException {
		this.property.clear();
	}

	@Override
	public boolean propertyExists(final String name) throws JMSException {
		return this.property.containsKey(name);
	}

	@Override
	public boolean getBooleanProperty(final String name) throws JMSException {
		return Boolean.parseBoolean(this.property.get(name));
	}

	@Override
	public byte getByteProperty(final String name) throws JMSException {
		return 0;
	}

	@Override
	public short getShortProperty(final String name) throws JMSException {
		return 0;
	}

	@Override
	public int getIntProperty(final String name) throws JMSException {
		return 0;
	}

	@Override
	public long getLongProperty(final String name) throws JMSException {
		return Long.parseLong(this.property.get(name));
	}

	@Override
	public float getFloatProperty(final String name) throws JMSException {
		return 0;
	}

	@Override
	public double getDoubleProperty(final String name) throws JMSException {
		return 0;
	}

	@Override
	public String getStringProperty(final String name) throws JMSException {
		return property.get(name);
	}

	@Override
	public Object getObjectProperty(final String name) throws JMSException {
		return null;
	}

	@Override
	public Enumeration<String> getPropertyNames() throws JMSException {
		return Collections.enumeration(this.property.keySet());
	}

	@Override
	public void setBooleanProperty(final String name, final boolean value) throws JMSException {
	}

	@Override
	public void setByteProperty(final String name, final byte value) throws JMSException {
	}

	@Override
	public void setShortProperty(final String name, final short value) throws JMSException {
	}

	@Override
	public void setIntProperty(final String name, final int value) throws JMSException {

	}

	@Override
	public void setLongProperty(final String name, final long value) throws JMSException {
	}

	@Override
	public void setFloatProperty(final String name, final float value) throws JMSException {
	}

	@Override
	public void setDoubleProperty(final String name, final double value) throws JMSException {
	}

	@Override
	public void setStringProperty(final String name, final String value) throws JMSException {
		this.property.put(name, value);
	}

	public MockTextMessage withStringProperty(final String name, final String value) {
		this.property.put(name, value);
		return this;
	}

	@Override
	public void setObjectProperty(final String name, final Object value) throws JMSException {
	}

	@Override
	public void acknowledge() throws JMSException {
	}

	@Override
	public void clearBody() throws JMSException {
	}

	@Override
	public void setText(final String string) throws JMSException {
		this.text = string;
	}

	public MockTextMessage withText(final String string) {
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
    public void setJMSDeliveryTime(final long deliveryTime) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public <T> T getBody(final Class<T> c) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBodyAssignableTo(final Class c) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

}
