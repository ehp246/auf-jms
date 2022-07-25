package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

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

    default Object body() {
        return null;
    }

    default BodyAs bodyAs() {
        if (body() == null) {
            return null;
        }

        return () -> body().getClass();
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

    default String groupId() {
        return null;
    }

    default int groupSeq() {
        return 0;
    }

    /**
     * Application properties.
     */
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
    static JmsDispatch toDispatch(At to, String type) {
        return toDispatch(to, type, null, UUID.randomUUID().toString());
    }

    /**
     * Single body value. Generated correlation id.
     * 
     * @param to
     * @param type
     * @param body
     * @return
     */
    static JmsDispatch toDispatch(At to, String type, Object body) {
        return toDispatch(to, type, body, UUID.randomUUID().toString());
    }

    /**
     * Single body value. Generated correlation id.
     * 
     * @param to
     * @param type
     * @param body
     * @param id
     * @return
     */
    static JmsDispatch toDispatch(At to, String type, Object body, String id) {
        return toDispatch(to, type, body, id, null);
    }

    /**
     * Single body value. Generated correlation id.
     * 
     * @param to
     * @param type
     * @param body
     * @param id
     * @param properties
     * @return
     */
    static JmsDispatch toDispatch(At to, String type, Object body, String id, Map<String, Object> properties) {
        return new JmsDispatch() {
            @Override
            public At to() {
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

    interface BodyAs {
        Class<?> type();

        static BodyAs of(Class<?> type) {
            return () -> type;
        }
    }
}
