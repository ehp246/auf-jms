package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.exception.JmsDispatchException;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.JmsNames;
import me.ehp246.aufjms.api.jms.ToJson;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 * @see EnableByJmsRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata,
 *      org.springframework.beans.factory.support.BeanDefinitionRegistry)
 */
public final class DefaultDispatchFnProvider implements JmsDispatchFnProvider, AutoCloseable {
    private final static Logger LOGGER = LogManager.getLogger(DefaultDispatchFnProvider.class);
    private final static Set<String> RESERVED_PROPERTIES = Set.of(JmsNames.GROUP_ID, JmsNames.GROUP_SEQ);

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
            if (listener instanceof final DispatchListener.OnDispatch onDispatch) {
                onDispatchs.add(onDispatch);
            }
            if (listener instanceof final DispatchListener.PreSend preSend) {
                preSends.add(preSend);
            }
            if (listener instanceof final DispatchListener.PostSend postSend) {
                postSends.add(postSend);
            }
            if (listener instanceof final DispatchListener.OnException onEx) {
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
            } catch (final JMSException e) {
                LOGGER.atError().withThrowable(e).log("Failed to create connection on factory '{}': {}",
                        connectionFactoryName::toString, e::getMessage);
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

                Session session = null;
                MessageProducer producer = null;
                TextMessage message = null;
                JmsMsg msg = null;
                try {
                    for (final var listener : DefaultDispatchFnProvider.this.onDispatchs) {
                        listener.onDispatch(dispatch);
                    }

                    /*
                     * If connection is not set, look for session in the context. It is an error if
                     * both are missing.
                     */
                    if (connection == null && AufJmsContext.getSession() == null) {
                        throw new IllegalStateException("No session available");
                    }

                    if (dispatch.to() == null || !OneUtil.hasValue(dispatch.to().name())) {
                        throw new IllegalArgumentException("To must be specified");
                    }

                    /*
                     * Validation
                     */
                    final var properties = Optional.ofNullable(dispatch.properties());
                    properties.map(Map::keySet).map(Set::stream)
                            .flatMap(keys -> keys.filter(key -> RESERVED_PROPERTIES.contains(key)).findAny())
                            .ifPresent(key -> {
                                throw new IllegalArgumentException("Un-allowed property name '" + key + "'");
                            });

                    /*
                     * Connection priority.
                     */
                    session = connection != null ? connection.createSession() : AufJmsContext.getSession();
                    producer = session.createProducer(null);
                    message = session.createTextMessage();

                    /*
                     * Fill the custom properties first so the framework ones won't get
                     */
                    for (final var entry : properties.orElseGet(HashMap<String, Object>::new).entrySet()) {
                        message.setObjectProperty(entry.getKey().toString(), entry.getValue());
                    }

                    final var to = dispatch.to() instanceof AtQueue ? session.createQueue(dispatch.to().name())
                            : session.createTopic(dispatch.to().name());

                    message.setJMSDestination(to);
                    /*
                     * JMS headers
                     */
                    message.setJMSType(dispatch.type());
                    message.setJMSCorrelationID(dispatch.correlationId());
                    if (dispatch.groupId() != null && !dispatch.groupId().isBlank()) {
                        message.setStringProperty(JmsNames.GROUP_ID, dispatch.groupId());
                        message.setIntProperty(JmsNames.GROUP_SEQ, dispatch.groupSeq());
                    }

                    if (dispatch.replyTo() != null) {
                        message.setJMSReplyTo(
                                dispatch.replyTo() instanceof AtQueue ? session.createQueue(dispatch.replyTo().name())
                                        : session.createTopic(dispatch.replyTo().name()));
                    }

                    message.setText(toPayload(dispatch));

                    producer.setDeliveryDelay(
                            Optional.ofNullable(dispatch.delay()).map(Duration::toMillis).orElse((long) 0));
                    producer.setTimeToLive(
                            Optional.ofNullable(dispatch.ttl()).map(Duration::toMillis).orElse((long) 0));

                    msg = TextJmsMsg.from(message);

                    // Call listeners on preSend
                    for (final var listener : DefaultDispatchFnProvider.this.preSends) {
                        listener.preSend(dispatch, msg);
                    }

                    producer.send(to, message);

                    // Call listeners on postSend
                    for (final var listener : DefaultDispatchFnProvider.this.postSends) {
                        listener.postSend(dispatch, msg);
                    }

                    return msg;
                } catch (final Exception e) {
                    for (final var listener : DefaultDispatchFnProvider.this.onExs) {
                        try {
                            listener.onException(dispatch, msg, e);
                        } catch (final Exception e1) {
                            LOGGER.atError().withThrowable(e1).log("Ignored: {}", e::getMessage);
                        }
                    }

                    throw new JmsDispatchException(e);
                } finally {
                    /*
                     * Producer is always created.
                     */
                    if (producer != null) {
                        try {
                            producer.close();
                        } catch (final Exception e) {
                            LOGGER.atError().withThrowable(e).log("Ignored: {}", e::getMessage);
                        }
                    }

                    /*
                     * Session is created locally and needs to be closed only when connection is not
                     * null.
                     */
                    if (connection != null && session != null) {
                        try {
                            session.close();
                        } catch (final Exception e) {
                            LOGGER.atError().withThrowable(e).log("Ignored: {}", e::getMessage);
                        }
                    }
                }

            }

            private String toPayload(final JmsDispatch dispatch) {
                final var body = dispatch.body();
                if (body == null) {
                    return null;
                }

                if (body instanceof final Supplier<?> supplier) {
                    return Optional.ofNullable(supplier.get()).map(Object::toString).orElse(null);
                }

                return toJson.apply(body, dispatch.bodyOf());
            }
        };
    }

    @Override
    public void close() {
        closeable.stream().forEach(t -> {
            try {
                t.close();
            } catch (final Exception e) {
                LOGGER.atError().withThrowable(e).log("Ignored: {}", e::getMessage);
            }
        });
        closeable.clear();
    }
}
