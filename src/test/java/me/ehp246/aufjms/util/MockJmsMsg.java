package me.ehp246.aufjms.util;

import java.time.Instant;

import javax.jms.Destination;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public class MockJmsMsg implements JmsMsg {
    private final String type;

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
    public long expiration() {
        // TODO Auto-generated method stub
        return 0;
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

}
