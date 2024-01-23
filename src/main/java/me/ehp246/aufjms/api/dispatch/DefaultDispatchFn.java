package me.ehp246.aufjms.api.dispatch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.exception.JmsDispatchFailedException;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsDispatchContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.JmsNames;
import me.ehp246.aufjms.api.jms.ToJson;
import me.ehp246.aufjms.api.spi.MsgMDC;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * Light-weight class that implements {@linkplain JmsDispatchFn}.
 * <p>
 * A {@linkplain DefaultDispatchFn} can be created on either a
 * {@linkplain ConnectionFactory} or a {@linkplain JMSContext}.
 * <p>
 * {@linkplain ConnectionFactory}-backed object is thread-safe while
 * {@linkplain JMSContext}-based is not.
 *
 * @author Lei Yang
 */
public final class DefaultDispatchFn implements JmsDispatchFn {
    private final Logger LOGGER = LoggerFactory.getLogger(JmsDispatchFn.class.getName());

    private final ToJson toJson;
    private final ConnectionFactory connectionFactory;
    private final JMSContext jmsContext;

    private final List<DispatchListener.OnDispatch> onDispatchs = new ArrayList<>();
    private final List<DispatchListener.PreSend> preSends = new ArrayList<>();
    private final List<DispatchListener.PostSend> postSends = new ArrayList<>();
    private final List<DispatchListener.OnException> onExs = new ArrayList<>();

    /**
     * Creates a {@linkplain DefaultDispatchFn} with the given
     * {@linkplain ConnectionFactory}.
     * <p>
     * Unlike {@linkplain JMSContext}-backed {@linkplain DefaultDispatchFn}, the
     * {@linkplain DefaultDispatchFn} backed-by a {@linkplain ConnectionFactory}
     * creates and closes {@linkplain JMSContext} for each invocation on
     * {@linkplain JmsDispatchFn#send(JmsDispatch)}. I.e., it is thread-safe.
     */
    public DefaultDispatchFn(final ConnectionFactory connectionFactory, final ToJson toJson,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.toJson = Objects.requireNonNull(toJson);
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
        this.jmsContext = null;

        initListeners(dispatchListeners);
    }

    /**
     * Creates a {@linkplain DefaultDispatchFn} with the given
     * {@linkplain JMSContext}.
     * <p>
     * Closing of the context is up to the caller. Once the context is closed, the
     * {@linkplain DefaultDispatchFn} is no longer functional and should be
     * discarded since the reference to the context can not be changed after
     * construction.
     * <p>
     * {@linkplain JMSContext} is not thread-safe. {@linkplain DefaultDispatchFn}
     * created on a context is not thread-safe either but it offers much better
     * performance when sending messages in batch.
     */
    public DefaultDispatchFn(final JMSContext jmsContex, final ToJson toJson,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.toJson = Objects.requireNonNull(toJson);
        this.connectionFactory = null;
        this.jmsContext = Objects.requireNonNull(jmsContex);

        initListeners(dispatchListeners);
    }

    public DefaultDispatchFn(final JMSContext jmsContex, final ToJson toJson) {
        super();
        this.toJson = Objects.requireNonNull(toJson);
        this.connectionFactory = null;
        this.jmsContext = Objects.requireNonNull(jmsContex);
    }

    private void initListeners(final List<DispatchListener> dispatchListeners) {
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
    public JmsMsg send(final JmsDispatch dispatch) {
        Objects.requireNonNull(dispatch);

        MsgMDC.set(dispatch);

        JMSContext localContext = null;
        TextMessage message = null;
        JmsMsg msg = null;
        try {
            for (final var listener : this.onDispatchs) {
                listener.onDispatch(dispatch);
            }

            localContext = this.jmsContext == null ? this.connectionFactory.createContext()
                    : this.jmsContext;

            message = localContext.createTextMessage();
            msg = TextJmsMsg.from(message);

            /*
             * Context properties, dispatch properties in ascending priority.
             */
            final Map<String, Object> properties = new HashMap<>(
                    Optional.ofNullable(dispatch.properties()).orElseGet(Map::of));

            for (final var entry : Optional.ofNullable(JmsDispatchContext.properties())
                    .orElseGet(Map::of).entrySet()) {
                properties.putIfAbsent(entry.getKey(), entry.getValue());
            }

            for (final var entry : properties.entrySet()) {
                final var key = entry.getKey();
                if (AufJmsConstants.RESERVED_PROPERTIES.contains(key)) {
                    throw new IllegalArgumentException("Un-supported property name '" + key + "'");
                }
                message.setObjectProperty(key, entry.getValue());
            }

            /*
             * JMS headers
             */
            message.setJMSType(dispatch.type());
            message.setJMSCorrelationID(dispatch.correlationId());
            if (dispatch.groupId() != null && !dispatch.groupId().isBlank()) {
                message.setStringProperty(JmsNames.GROUP_ID, dispatch.groupId());
                message.setIntProperty(JmsNames.GROUP_SEQ, dispatch.groupSeq());
            }

            message.setJMSReplyTo(toJMSDestintation(localContext, dispatch.replyTo()));

            message.setText(toPayload(dispatch));

            final var producer = localContext.createProducer()
                    .setDeliveryDelay(Optional.ofNullable(dispatch.delay()).map(Duration::toMillis)
                            .orElse((long) 0))
                    .setTimeToLive(Optional.ofNullable(dispatch.ttl()).map(Duration::toMillis)
                            .orElse((long) 0));

            // Call listeners on preSend
            for (final var listener : this.preSends) {
                listener.preSend(dispatch, msg);
            }

            producer.send(toJMSDestintation(localContext, dispatch.to()), message);

            // Call listeners on postSend suppressing exceptions.
            for (final var listener : this.postSends) {
                try {
                    listener.postSend(dispatch, msg);
                } catch (final Exception e) {
                    LOGGER.atTrace().setCause(e).setMessage("Listener {} failed, ignoring: {}")
                            .addArgument(listener::toString).addArgument(e::getMessage).log();
                }
            }

            return msg;
        } catch (final Exception e) {
            for (final var listener : this.onExs) {
                try {
                    listener.onException(dispatch, msg, e);
                } catch (final Exception e1) {
                    LOGGER.atTrace().setCause(e1).setMessage("Listener {} failed, ignoring: {}")
                            .addArgument(listener::toString).addArgument(e1::getMessage).log();
                }
            }

            throw new JmsDispatchFailedException("Dispatch failed, CorrelationId="
                    + dispatch.correlationId() + ", " + e.getMessage(), e);
        } finally {
            if (this.jmsContext == null && localContext != null) {
                try {
                    localContext.close();
                } catch (final Exception e) {
                    LOGGER.atTrace().setCause(e).setMessage("JMSCOntext close failed, ignoring: {}")
                            .addArgument(e::getMessage).log();
                }
            }

            MsgMDC.clear(dispatch);
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

    private Destination toJMSDestintation(final JMSContext jmsContext, final At to) {
        if (to == null || !OneUtil.hasValue(to.name())) {
            return null;
        }

        return to instanceof AtQueue ? jmsContext.createQueue(to.name())
                : jmsContext.createTopic(to.name());
    }
}
