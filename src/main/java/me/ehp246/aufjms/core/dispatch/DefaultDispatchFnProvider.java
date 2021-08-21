package me.ehp246.aufjms.core.dispatch;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.DispatchFn;
import me.ehp246.aufjms.api.dispatch.DispatchFnProvider;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.exception.DispatchFnException;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.ContextProvider;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.MsgPropertyName;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultDispatchFnProvider implements DispatchFnProvider {
    private final static Logger LOGGER = LogManager.getLogger(DefaultDispatchFnProvider.class);

    private final ContextProvider ctxProvider;
    private final ToJson toJson;
    private final List<DispatchListener> listeners;

    public DefaultDispatchFnProvider(final ContextProvider ctxProvider, final ToJson jsonFn,
            final List<DispatchListener> dispatchListeners) {
        super();
        this.ctxProvider = Objects.requireNonNull(ctxProvider);
        this.toJson = jsonFn;
        this.listeners = dispatchListeners == null ? List.of() : Collections.unmodifiableList(dispatchListeners);
    }

    @Override
    public DispatchFn get(final String contextName) {
        return new DispatchFn() {
            private final JMSContext jmsCtx = ctxProvider.get(contextName);

            @Override
            public JmsMsg dispatch(JmsDispatch dispatch) {
                LOGGER.atTrace().log("Sending {} {} to {} ", dispatch.type(), dispatch.correlationId(),
                        dispatch.destination().name().toString());

                final var message = jmsCtx.createTextMessage();

                try {
                    message.setText(DefaultDispatchFnProvider.this.toJson.apply(dispatch.bodyValues()));

                    // Fill the customs first so the framework ones won't get over-written.
//                    final var map = Optional.ofNullable(msg.getPropertyMap()).orElseGet(HashMap<String, String>::new);
//                    for (final String key : map.keySet()) {
//                        message.setStringProperty(key, map.get(key));
//                    }
                    /*
                     * JMS headers
                     */
                    message.setJMSReplyTo(toJMSDestintation(dispatch.replyTo()));
                    message.setJMSType(dispatch.type());
                    message.setJMSCorrelationID(dispatch.correlationId());
                    if (OneUtil.hasValue(dispatch.groupId())) {
                        message.setStringProperty(MsgPropertyName.GROUP_ID, dispatch.groupId());
                    }
                    message.setIntProperty(MsgPropertyName.GROUP_SEQ,
                            Optional.ofNullable(dispatch.groupSeq()).map(Integer::intValue).orElse(0));

                    /*
                     * Framework headers
                     */
                    // message.setStringProperty(MsgPropertyName.Invoking, msg.getInvoking());
                    // message.setBooleanProperty(MsgPropertyName.ServerThrown, msg.isException());

                    message.setText(toJson.apply(dispatch.bodyValues()));
                } catch (final JMSException e) {
                    LOGGER.atError().log("Message failed: destination {}, type {}, correclation id {}",
                            dispatch.destination().toString(), dispatch.type(), dispatch.correlationId(), e);
                    throw new DispatchFnException(e);
                }

                jmsCtx.createProducer().setTimeToLive(Optional.ofNullable(dispatch.ttl().toMillis()).orElse((long) 0))
                        .send(toJMSDestintation(dispatch.destination()), message);

                LOGGER.atTrace().log("Sent {} {}", dispatch.type(), dispatch.correlationId());

                final var msg = TextJmsMsg.from(message);
                // Call listeners
                DefaultDispatchFnProvider.this.listeners.stream()
                        .forEach(listener -> listener.onDispatch(msg, dispatch));

                return msg;
            }

            private Destination toJMSDestintation(AtDestination at) {
                if (at == null || !OneUtil.hasValue(at.name())) {
                    return null;
                }

                return at.type() == DestinationType.QUEUE ? jmsCtx.createQueue(at.name())
                        : jmsCtx.createTopic(at.name());
            }
        };
    }
}
