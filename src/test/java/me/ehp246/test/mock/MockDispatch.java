package me.ehp246.test.mock;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
public class MockDispatch implements JmsDispatch {
    private final String type = UUID.randomUUID().toString();
    private final String correlId = UUID.randomUUID().toString();
    private final String groupId = UUID.randomUUID().toString();
    private final Map<String, Object> properties = new HashMap<>();

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
    public Object body() {
        return null;
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

    @Override
    public Map<String, Object> properties() {
        return this.properties;
    }

    public MockDispatch withProperty(final String key, final Object value) {
        this.properties.put(key, value);
        return this;
    }
}
