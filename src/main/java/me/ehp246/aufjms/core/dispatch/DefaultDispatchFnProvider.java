package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.BodySupplier;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.exception.JmsDispatchFnException;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.To;
import me.ehp246.aufjms.api.jms.ToQueue;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultDispatchFnProvider implements JmsDispatchFnProvider, AutoCloseable {
    private final static Logger LOGGER = LogManager.getLogger(DefaultDispatchFnProvider.class);

    private final ConnectionFactoryProvider cfProvider;
    private final ToJson toJson;
    private final List<DispatchListener> listeners;
    private final Set<Connection> closeable = ConcurrentHashMap.newKeySet();

    public DefaultDispatchFnProvider(final ConnectionFactoryProvider cfProvider, final ToJson jsonFn,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.cfProvider = Objects.requireNonNull(cfProvider);
        this.toJson = Objects.requireNonNull(jsonFn);
        this.listeners = dispatchListeners == null ? List.of() : Collections.unmodifiableList(dispatchListeners);
    }

    @Override
    public JmsDispatchFn get(final String connectionFactoryName) {
        final Connection connection;
        if (connectionFactoryName != null) {
            try {
                connection = cfProvider.get(connectionFactoryName).createConnection();
            } catch (Exception e) {
                LOGGER.atError().log("Failed to create connection on factory {}:{}", connectionFactoryName,
                        e.getMessage());
                throw new JmsDispatchFnException(e);
            }

            this.closeable.add(connection);
        } else {
            connection = null;
        }

        return new JmsDispatchFn() {
            private final Logger LOGGER = LogManager
                    .getLogger(JmsDispatchFn.class.getName() + "@" + connectionFactoryName);

            @Override
            public JmsMsg send(JmsDispatch dispatch) {
                /*
                 * If connection is not set, look for the context. It's an error, if both are
                 * missing.
                 */
                if (connection == null && AufJmsContext.getSession() == null) {
                    throw new JmsDispatchFnException("No session can be created");
                }

                LOGGER.atTrace().log("Sending {} {} to {} on {}", dispatch.type(), dispatch.correlationId(),
                        dispatch.to().name().toString(), connectionFactoryName);

                Session session = null;
                MessageProducer producer = null;
                TextMessage message = null;
                JmsMsg msg = null;
                try {
                    // Connection priority.
                    session = connection != null ? connection.createSession() : AufJmsContext.getSession();
                    producer = session.createProducer(null);
                    message = session.createTextMessage();
                    msg = TextJmsMsg.from(message);

                    // Fill the custom properties first so the framework ones won't get
                    // overwritten.
                    for (final var entry : Optional.ofNullable(dispatch.properties())
                            .orElseGet(HashMap<String, Object>::new).entrySet()) {
                        message.setObjectProperty(entry.getKey().toString(), entry.getValue());
                    }

                    /*
                     * JMS headers
                     */
                    message.setJMSReplyTo(toJMSDestintation(session, dispatch.replyTo()));
                    message.setJMSType(dispatch.type());
                    message.setJMSCorrelationID(dispatch.correlationId());
                    message.setText(toText(dispatch));

                    producer.setDeliveryDelay(
                            Optional.ofNullable(dispatch.delay()).map(Duration::toMillis).orElse((long) 0));
                    producer.setTimeToLive(
                            Optional.ofNullable(dispatch.ttl()).map(Duration::toMillis).orElse((long) 0));

                    // Call listeners pre-send
                    for (final var listener : DefaultDispatchFnProvider.this.listeners) {
                        listener.onDispatch(msg, dispatch);
                    }

                    producer.send(toJMSDestintation(session, dispatch.to()), message);

                    LOGGER.atTrace().log("Sent {} {}", dispatch.type(), dispatch.correlationId());

                    // Call listeners post-send
                    for (final var listener : DefaultDispatchFnProvider.this.listeners) {
                        listener.onSent(msg, dispatch);
                    }

                    return msg;
                } catch (final Exception e) {
                    LOGGER.atError().log("Message failed: destination {}, type {}, correclation id {}",
                            dispatch.to().toString(), dispatch.type(), dispatch.correlationId(), e);

                    // Call listeners on-exception
                    for (final var listener : DefaultDispatchFnProvider.this.listeners) {
                        listener.onException(e, msg, dispatch);
                    }

                    throw new JmsDispatchFnException(e);
                } finally {
                    /*
                     * Producer is always created.
                     */
                    if (producer != null) {
                        try {
                            producer.close();
                        } catch (JMSException e) {
                            LOGGER.atError().log("Failed to close producer. Ignored", e);
                        }
                    }

                    /*
                     * Session is created locally only when connection is null.
                     */
                    if (connection != null && session != null) {
                        try {
                            session.close();
                        } catch (JMSException e) {
                            LOGGER.atError().log("Failed to close session. Ignored.", e);
                        }
                    }
                }

            }

            private String toText(JmsDispatch dispatch) {
                final var bodyValues = dispatch.bodyValues();
                if (bodyValues == null || bodyValues.size() == 0) {
                    return null;
                }

                return bodyValues.stream().filter(value -> value instanceof BodySupplier).findAny()
                        .map(value -> ((BodySupplier) value).get())
                        .orElseGet(() -> DefaultDispatchFnProvider.this.toJson.apply(bodyValues));
            }
        };
    }

    private static Destination toJMSDestintation(Session session, To at) throws JMSException {
        if (at == null || !OneUtil.hasValue(at.name())) {
            return null;
        }

        return at instanceof ToQueue ? session.createQueue(at.name()) : session.createTopic(at.name());
    }

    @Override
    public void close() {
        closeable.stream().forEach(t -> {
            try {
                t.close();
            } catch (JMSException e) {
                LOGGER.atError().log("Failed to close connection. Ignored", e);
            }
        });
        closeable.clear();
    }
}
