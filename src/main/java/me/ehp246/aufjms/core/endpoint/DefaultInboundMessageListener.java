package me.ehp246.aufjms.core.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.lang.Nullable;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.endpoint.InvocableDispatcher;
import me.ehp246.aufjms.api.endpoint.MsgConsumer;
import me.ehp246.aufjms.api.endpoint.MsgInvocableFactory;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 *
 */
final class DefaultInboundMessageListener implements SessionAwareMessageListener<Message> {
    private final static Logger LOGGER = LogManager.getLogger(InboundEndpoint.class);

    private final InvocableDispatcher dispatcher;
    private final MsgInvocableFactory invocableFactory;
    private final MsgConsumer defaultConsumer;

    DefaultInboundMessageListener(final InvocableDispatcher dispatcher, final MsgInvocableFactory invocableFactory,
            @Nullable final MsgConsumer defaultConsumer) {
        super();
        this.dispatcher = dispatcher;
        this.invocableFactory = invocableFactory;
        this.defaultConsumer = defaultConsumer;
    }

    @Override
    public void onMessage(final Message message, final Session session) throws JMSException {
        if (!(message instanceof final TextMessage textMessage)) {
            throw new IllegalArgumentException("Un-supported message type");
        }

        final var msg = TextJmsMsg.from(textMessage);
        try {
            AufJmsContext.set(session);
            Log4jContext.set(msg);

            LOGGER.atDebug().log("Consuming {}", msg::id);
            LOGGER.atTrace().log("Consuming {}", msg::text);

            final var invocable = invocableFactory.get(msg);

            if (invocable == null) {
                if (defaultConsumer == null) {
                    throw new UnknownTypeException(msg);
                } else {
                    defaultConsumer.accept(msg);
                    return;
                }
            }

            LOGGER.atDebug().log("Dispatching {}", () -> invocable.method().toString());

            dispatcher.dispatch(invocable, msg);

            LOGGER.atDebug().log("Consumed {}", msg::id);
        } catch (final Exception e) {
            LOGGER.atError().withThrowable(e).log("Message failed: {}", e::getMessage);

            throw e;
        } finally {
            AufJmsContext.clearSession();
            Log4jContext.clear();
        }
    }

}
