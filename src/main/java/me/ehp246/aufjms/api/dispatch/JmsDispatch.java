package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import me.ehp246.aufjms.api.jms.At;

/**
 * The abstraction of a fully-realized out-bound JMS message.
 * <p>
 * A {@link JmsDispatch} is meant to be dispatched/sent by a
 * {@link JmsDispatchFn}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface JmsDispatch {
    At to();

    default String type() {
        return null;
    }

    default String correlationId() {
        return null;
    }

    default List<?> bodyValues() {
        return null;
    }

    default At replyTo() {
        return null;
    }

    /**
     * Defines if the dispatch has a TTL.
     * <p>
     * The default, <code>null</code>, means no TTL to set.
     */
    default Duration ttl() {
        return null;
    }

    default Map<String, Object> properties() {
        return Map.of();
    }

    default Duration delay() {
        return null;
    }
}
