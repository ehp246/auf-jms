package me.ehp246.aufjms.util;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;

/**
 * @author Lei Yang
 *
 */
public class MockDispatch implements JmsDispatch {
    private final String type = UUID.randomUUID().toString();
    private final Destination destination = new ActiveMQQueue(TestQueueListener.DESTINATION_NAME);
    private final String correlId = UUID.randomUUID().toString();
    private final String groupId = UUID.randomUUID().toString();

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
    public Duration ttl() {
        return Duration.ZERO;
    }

    @Override
    public String groupId() {
        return groupId;
    }

    @Override
    public Integer groupSeq() {
        return Integer.valueOf(110);
    }

}
