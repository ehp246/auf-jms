package me.ehp246.test.mock;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
public class MockDispatch implements JmsDispatch {
    private final String type = UUID.randomUUID().toString();
    private final String correlId = UUID.randomUUID().toString();
    private final String groupId = UUID.randomUUID().toString();

    @Override
    public String type() {
        return type;
    }

    @Override
    public At to() {
        return new AtQueue() {

            @Override
            public String name() {
                return TestQueueListener.DESTINATION_NAME;
            }
        };
    }

    @Override
    public List<?> body() {
        return List.of();
    }

    @Override
    public String correlationId() {
        return correlId;
    }

    @Override
    public At replyTo() {
        return null;
    }

    @Override
    public Duration ttl() {
        return Duration.ZERO;
    }

}
