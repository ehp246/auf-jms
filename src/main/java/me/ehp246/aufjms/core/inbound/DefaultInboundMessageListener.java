package me.ehp246.aufjms.core.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.lang.Nullable;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.InvocableDispatcher;
import me.ehp246.aufjms.api.inbound.InvocableFactory;
import me.ehp246.aufjms.api.inbound.MsgConsumer;
import me.ehp246.aufjms.api.spi.MsgMDC;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 */
public final class DefaultInboundMessageListener implements SessionAwareMessageListener<Message> {
    private final static Logger LOGGER = LoggerFactory.getLogger(InboundEndpoint.class);

    private final InvocableDispatcher dispatcher;
    private final InvocableFactory invocableFactory;
    private final MsgConsumer defaultConsumer;

    public DefaultInboundMessageListener(final InvocableDispatcher dispatcher,
            final InvocableFactory invocableFactory, @Nullable final MsgConsumer defaultConsumer) {
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
        try (final var closeble = MsgMDC.set(msg);) {
            LOGGER.atDebug().addMarker(AufJmsConstants.HEADERS).setMessage("{}, {}, {}")
                    .addArgument(msg::destination).addArgument(msg::type)
                    .addArgument(msg::correlationId).log();
            LOGGER.atTrace().addMarker(AufJmsConstants.BODY).setMessage("{}").addArgument(msg::text)
                    .log();

            final var invocable = invocableFactory.get(msg);

            if (invocable == null) {
                if (defaultConsumer == null) {
                    throw new UnknownTypeException(msg);
                } else {
                    defaultConsumer.accept(msg);
                    return;
                }
            }

            dispatcher.dispatch(invocable, msg);
        } catch (final JMSException | RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
