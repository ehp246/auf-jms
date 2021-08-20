package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.List;

import me.ehp246.aufjms.api.jms.AtDestination;

/**
 * The abstraction of a fully-realized out-bound JMS message.
 * <p>
 * A {@link JmsDispatch} is meant to be dispatched/sent by a {@link DispatchFn}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface JmsDispatch {
    AtDestination destination();

    default String type() {
        return null;
    }

    default String correlationId() {
        return null;
    }

    default List<?> bodyValues() {
        return null;
    }

    default AtDestination replyTo() {
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

    default String groupId() {
        return null;
    }

    default Integer groupSeq() {
        return null;
    }
}
