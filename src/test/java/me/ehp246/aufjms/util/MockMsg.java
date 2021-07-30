package me.ehp246.aufjms.util;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;

import me.ehp246.aufjms.api.jms.Msg;

/**
 * @author Lei Yang
 *
 */
public class MockMsg implements Msg {
    private final String id = UUID.randomUUID().toString();
    private final String type = UUID.randomUUID().toString();
    private final Destination destination = new ActiveMQQueue(AppConfig.TEST_QUEUE);
    private final String correlId = UUID.randomUUID().toString();

    @Override
    public String id() {
        return id;
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
    public List<?> bodyValues() {
        return List.of();
    }

    @Override
    public String correlationId() {
        return correlId;
    }

    @Override
    public Destination replyTo() {
        return null;
    }

    @Override
    public long expiration() {
        return 0;
    }

    @Override
    public long ttl() {
        return 0;
    }

    @Override
    public Instant timestamp() {
        return null;
    }

    @Override
    public <T> T property(String name, Class<T> type) {
        return null;
    }

    @Override
    public String groupId() {
        return null;
    }

    @Override
    public int groupSeq() {
        return 0;
    }

}
