package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.BodyPublisher;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JmsMsg;
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
    private final List<DispatchListener.OnDispatch> onDispatchs = new ArrayList<>();
    private final List<DispatchListener.PreSend> preSends = new ArrayList<>();
    private final List<DispatchListener.PostSend> postSends = new ArrayList<>();
    private final List<DispatchListener.OnException> onExs = new ArrayList<>();
    private final Set<Connection> closeable = ConcurrentHashMap.newKeySet();

    public DefaultDispatchFnProvider(final ConnectionFactoryProvider cfProvider, final ToJson jsonFn,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.cfProvider = Objects.requireNonNull(cfProvider);
        this.toJson = Objects.requireNonNull(jsonFn);

        for (final var listener : dispatchListeners == null ? List.of() : dispatchListeners) {
            if (listener instanceof DispatchListener.OnDispatch onDispatch) {
                onDispatchs.add(onDispatch);
            } else if (listener instanceof DispatchListener.PreSend preSend) {
                preSends.add(preSend);
            } else if (listener instanceof DispatchListener.PostSend postSend) {
                postSends.add(postSend);
            } else if (listener instanceof DispatchListener.OnException onEx) {
                onExs.add(onEx);
            }
        }
    }

    @Override
    public JmsDispatchFn get(final String connectionFactoryName) {
        final Connection connection;
        if (connectionFactoryName != null) {
            try {
                connection = cfProvider.get(connectionFactoryName).createConnection();
            } catch (JMSException e) {
                LOGGER.atError().log("Failed to create connection on factory '{}': {}", connectionFactoryName,
                        e.getMessage());
                throw new JMSRuntimeException(e.getErrorCode(), e.getMessage(), e);
            }

            this.closeable.add(connection);
        } else {
            connection = null;
        }

        return new JmsDispatchFn() {
            private final Logger LOGGER = LogManager
                    .getLogger(JmsDispatchFn.class.getName() + "@" + connectionFactoryName);

            @Override
            public JmsMsg send(final JmsDispatch dispatch) {
                Objects.requireNonNull(dispatch);

                LOGGER.atTrace().log("Sending '{}' '{}' to '{}' on '{}'", dispatch.type(), dispatch.correlationId(),
                        dispatch.to(), connectionFactoryName);

                Session session = null;
                MessageProducer producer = null;
                TextMessage message = null;
                JmsMsg msg = null;
                try {
                    for (final var listener : DefaultDispatchFnProvider.this.onDispatchs) {
                        listener.onDispatch(dispatch);
                    }

                    /*
                     * If connection is not set, look for one in the context. It is an error, if
                     * both are missing.
                     */
                    if (connection == null && AufJmsContext.getSession() == null) {
                        throw new IllegalStateException("No session available");
                    }

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

                    // Call listeners on preSend
                    for (final var listener : DefaultDispatchFnProvider.this.preSends) {
                        listener.preSend(dispatch, msg);
                    }

                    producer.send(toJMSDestintation(session, dispatch.to()), message);

                    LOGGER.atTrace().log("Sent {} {}", dispatch.type(), dispatch.correlationId());

                    // Call listeners on postSend
                    for (final var listener : DefaultDispatchFnProvider.this.postSends) {
                        listener.postSend(dispatch, msg);
                    }

                    return msg;
                } catch (final Exception e) {
                    LOGGER.atError().log("Message failed: destination {}, type {}, correclation id {}",
                            dispatch.to().toString(), dispatch.type(), dispatch.correlationId(), e);

                    try {
                        for (final var listener : DefaultDispatchFnProvider.this.onExs) {
                            listener.onException(dispatch, msg, e);
                        }
                    } catch (RuntimeException ex) {
                        throw ex;
                    }

                    // Re-throw anything unchecked.
                    if (e instanceof RuntimeException re) {
                        throw re;
                    }

                    // Wrap checked.
                    throw OneUtil.ensureRuntime(e);
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
                final var body = dispatch.body();
                if (body == null) {
                    return null;
                }

                if (body instanceof BodyPublisher publisher) {
                    return publisher.get();
                }

                return toJson.apply(List.of(new ToJson.From(body, dispatch.bodyAs().type())));
            }
        };
    }

    private static Destination toJMSDestintation(Session session, At to) throws JMSException {
        if (to == null || !OneUtil.hasValue(to.name())) {
            return null;
        }

        return to instanceof AtQueue ? session.createQueue(to.name()) : session.createTopic(to.name());
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
