package me.ehp246.aufjms.util;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.AtDestination;

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
    public AtDestination at() {
        return new AtDestination() {

            @Override
            public String name() {
                return TestQueueListener.DESTINATION_NAME;
            }
        };
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
    public AtDestination replyTo() {
        return null;
    }

    @Override
    public Duration ttl() {
        return Duration.ZERO;
    }

}
