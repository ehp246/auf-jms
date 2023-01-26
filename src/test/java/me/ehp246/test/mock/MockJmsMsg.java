package me.ehp246.test.mock;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.mockito.Mockito;

import jakarta.jms.Destination;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public class MockJmsMsg implements JmsMsg {
    private final TextMessage message = Mockito.mock(TextMessage.class);
    private final Session session = Mockito.mock(Session.class);
    private final String type;
    private final String correlId = UUID.randomUUID().toString();
    private final Destination destination = new ActiveMQQueue(UUID.randomUUID().toString());
    private final Map<String, Object> properties = new HashMap<>();
    private String text;

    public MockJmsMsg() {
        super();
        type = null;
    }

    public MockJmsMsg(final String type) {
        super();
        this.type = type;
    }

    public MockJmsMsg withProperty(final String key, final Object value) {
        this.properties.put(key, value);
        return this;
    }

    public MockJmsMsg withText(final String text) {
        this.text = text;
        return this;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public Destination destination() {
        return destination;
    }

    @Override
    public String id() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String text() {
        return this.text;
    }

    @Override
    public Instant expiration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Instant timestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String invoking() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T property(final String name, final Class<T> type) {
        return (T) this.properties.get(name);
    }

    @Override
    public TextMessage message() {
        return message;
    }

    @Override
    public String correlationId() {
        return correlId;
    }

    @Override
    public Destination replyTo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String groupId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int groupSeq() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Set<String> propertyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int deliveryCount() {
        return 1;
    }

    @Override
    public boolean redelivered() {
        return false;
    }
}
