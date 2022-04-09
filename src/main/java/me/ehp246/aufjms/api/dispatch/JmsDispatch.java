package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import me.ehp246.aufjms.api.jms.To;

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
    To to();

    default String type() {
        return null;
    }

    default String correlationId() {
        return null;
    }

    default Object body() {
        return null;
    }

    default To replyTo() {
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
        return null;
    }

    default Duration delay() {
        return null;
    }

    /**
     * Single body value. Generated correlation id.
     * 
     * @param to
     * @param type
     * @return
     */
    static JmsDispatch toDispatch(To to, String type) {
        return toDispatch(to, type, UUID.randomUUID().toString(), null);
    }

    /**
     * Single body value. Generated correlation id.
     * 
     * @param to
     * @param type
     * @param body
     * @return
     */
    static JmsDispatch toDispatch(To to, String type, Object body) {
        return toDispatch(to, type, UUID.randomUUID().toString(), body);
    }

    /**
     * Single body value. Generated correlation id.
     * 
     * @param to
     * @param type
     * @param id
     * @param body
     * @return
     */
    static JmsDispatch toDispatch(To to, String type, String id, Object body) {
        return toDispatch(to, type, id, null, body);
    }

    /**
     * Single body value. Generated correlation id.
     * 
     * @param to
     * @param type
     * @param id
     * @param properties
     * @param body
     * @return
     */
    static JmsDispatch toDispatch(To to, String type, String id, Map<String, Object> properties, Object body) {
        return new JmsDispatch() {
            @Override
            public To to() {
                return to;
            }

            @Override
            public String type() {
                return type;
            }

            @Override
            public String correlationId() {
                return id;
            }

            @Override
            public Object body() {
                return body;
            }

            @Override
            public Map<String, Object> properties() {
                return properties;
            }
        };
    }
}
