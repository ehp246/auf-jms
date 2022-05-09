package me.ehp246.aufjms.util;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public class MockJmsMsg implements JmsMsg, MsgContext {
    private final String type;
    private final String correlId = UUID.randomUUID().toString();

    public MockJmsMsg() {
        super();
        type = null;
    }

    public MockJmsMsg(String type) {
        super();
        this.type = type;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public Destination destination() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String id() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String text() {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    public <T> T property(String name, Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TextMessage message() {
        // TODO Auto-generated method stub
        return null;
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
    public JmsMsg msg() {
        return this;
    }

    @Override
    public Session session() {
        return null;
    }

    @Override
    public int deliveryCount() {
        return 1;
    }
}
