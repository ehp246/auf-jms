package me.ehp246.aufjms.core.byjms;

import java.util.Objects;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.ToJson;
import me.ehp246.aufjms.api.exception.MsgFnException;
import me.ehp246.aufjms.api.jms.DispatchFn;
import me.ehp246.aufjms.api.jms.DispatchFnProvider;
import me.ehp246.aufjms.api.jms.MsgPropertyName;
import me.ehp246.aufjms.api.jms.NamedConnectionProvider;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultDispatchFnProvider implements DispatchFnProvider {
    private final static Logger LOGGER = LogManager.getLogger(DefaultDispatchFnProvider.class);

    private final NamedConnectionProvider connProvider;
    private final ToJson toJson;

    public DefaultDispatchFnProvider(final NamedConnectionProvider cons, final ToJson jsonFn) {
        super();
        this.connProvider = Objects.requireNonNull(cons);
        this.toJson = jsonFn;
    }

    @Override
    public DispatchFn get(final String connectionName) {
        final var connection = connProvider.get(connectionName);
        return msg -> {
            LOGGER.trace("Sending {}:{} to {} ", msg.correlationId(), msg.type(), msg.destination().toString());

            try (final Session session = connection.createSession(true,
                    Session.SESSION_TRANSACTED)) {
                final var message = session.createTextMessage();
                message.setText(this.toJson.toJson(msg.bodyValues()));

                // Fill the customs first so the framework ones won't get over-written.
//                final var map = Optional.ofNullable(msg.getPropertyMap()).orElseGet(HashMap<String, String>::new);
//                for (final String key : map.keySet()) {
//                    message.setStringProperty(key, map.get(key));
//                }

                message.setJMSDestination(msg.destination());
                message.setJMSReplyTo(msg.replyTo());

                /*
                 * JMS headers
                 */
                message.setJMSType(msg.type());
                message.setJMSCorrelationID(msg.correlationId());
                message.setStringProperty(MsgPropertyName.GROUP_ID, msg.groupId());
                message.setIntProperty(MsgPropertyName.GROUP_SEQ, msg.groupSeq());

                /*
                 * Framework headers
                 */
                // message.setStringProperty(MsgPropertyName.Invoking, msg.getInvoking());
                // message.setBooleanProperty(MsgPropertyName.ServerThrown, msg.isException());

                message.setText(toJson.toJson(msg.bodyValues()));

                try (final MessageProducer producer = session.createProducer(msg.destination())) {

                    producer.send(message);

                    session.commit();

                    LOGGER.trace("Sent {} ", msg.correlationId());

                    return message;
                }
            } catch (final JMSException e) {
                LOGGER.error("Failed to send: to {}, type {}, correclation id {}", msg.destination().toString(),
                        msg.type(),
                        msg.correlationId(), e);
                throw new MsgFnException(e);
            }
        };
    }
}
