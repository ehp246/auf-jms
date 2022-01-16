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
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.DispatchFn;
import me.ehp246.aufjms.api.dispatch.DispatchFnProvider;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.exception.DispatchFnException;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultDispatchFnProvider implements DispatchFnProvider, AutoCloseable {
    private final static Logger LOGGER = LogManager.getLogger(DefaultDispatchFnProvider.class);

    private final ConnectionFactoryProvider cfProvider;
    private final ToJson toJson;
    private final List<DispatchListener> listeners;
    private final Set<Connection> connSet = ConcurrentHashMap.newKeySet();

    public DefaultDispatchFnProvider(final ConnectionFactoryProvider cfProvider, final ToJson jsonFn,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.cfProvider = Objects.requireNonNull(cfProvider);
        this.toJson = Objects.requireNonNull(jsonFn);
        this.listeners = dispatchListeners == null ? List.of() : Collections.unmodifiableList(dispatchListeners);
    }

    @Override
    public DispatchFn get(final String connectionFactoryName) {
        Objects.requireNonNull(connectionFactoryName, "ConnectionFactory name required");
        final Connection connection;
        try {
            connection = cfProvider.get(connectionFactoryName).createConnection();
        } catch (JMSException e) {
            LOGGER.atError().log("Failed to create connection on factory {}:{}", connectionFactoryName, e.getMessage());
            throw new DispatchFnException(e);
        }

        this.connSet.add(connection);

        return new DispatchFn() {
            private final Logger LOGGER = LogManager
                    .getLogger(DispatchFn.class.getName() + "@" + connectionFactoryName);

            @Override
            public JmsMsg dispatch(JmsDispatch dispatch) {
                LOGGER.atTrace().log("Sending {} {} to {} on {} ", dispatch.type(), dispatch.correlationId(),
                        dispatch.at().name().toString(), connection.toString());

                try (final var session = connection.createSession();
                        final var producer = session.createProducer(null);) {
                    final var message = session.createTextMessage();

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
                    message.setText(DefaultDispatchFnProvider.this.toJson.apply(dispatch.bodyValues()));

                    producer.setDeliveryDelay(
                            Optional.ofNullable(dispatch.delay()).map(Duration::toMillis).orElse((long) 0));
                    producer.setTimeToLive(
                            Optional.ofNullable(dispatch.ttl()).map(Duration::toMillis).orElse((long) 0));

                    producer.send(toJMSDestintation(session, dispatch.at()), message);

                    LOGGER.atTrace().log("Sent {} {}", dispatch.type(), dispatch.correlationId());

                    final var msg = TextJmsMsg.from(message);
                    // Call listeners
                    DefaultDispatchFnProvider.this.listeners.stream()
                            .forEach(listener -> listener.onDispatch(msg, dispatch));

                    return msg;
                } catch (final JMSException e) {
                    LOGGER.atError().log("Message failed: destination {}, type {}, correclation id {}",
                            dispatch.at().toString(), dispatch.type(), dispatch.correlationId(), e);
                    throw new DispatchFnException(e);
                }

            }
        };
    }

    private static Destination toJMSDestintation(Session session, AtDestination at) throws JMSException {
        if (at == null || !OneUtil.hasValue(at.name())) {
            return null;
        }

        return at.type() == DestinationType.QUEUE ? session.createQueue(at.name()) : session.createTopic(at.name());
    }

    @Override
    public void close() throws Exception {
        connSet.stream().forEach(t -> {
            try {
                t.close();
            } catch (JMSException e) {
                LOGGER.atError().log("Failed to close connection", e);
            }
        });
        connSet.clear();
    }
}
