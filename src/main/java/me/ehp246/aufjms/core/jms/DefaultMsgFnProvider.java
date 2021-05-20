package me.ehp246.aufjms.core.jms;

import java.util.Objects;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.JsonFn;
import me.ehp246.aufjms.api.exception.MsgFnException;
import me.ehp246.aufjms.api.jms.MsgFn;
import me.ehp246.aufjms.api.jms.MsgPropertyName;

/**
 * @author Lei Yang
 *
 */
public final class DefaultMsgFnProvider {
    private final static Logger LOGGER = LogManager.getLogger(MsgFn.class);

    private final Connection connection;
    private final JsonFn jsonFn;

    public DefaultMsgFnProvider(final Connection connection, final JsonFn jsonFn) {
        super();
        this.connection = Objects.requireNonNull(connection);
        this.jsonFn = jsonFn;
    }

    public MsgFn get() {

        return msg -> {
            LOGGER.trace("Sending {}:{} to {} ", msg.correlationId(), msg.type(), msg.destination().toString());

            try (final Session session = connection.createSession(true, Session.SESSION_TRANSACTED)) {

                final var message = session.createTextMessage();
                message.setText(this.jsonFn.toJson(msg.bodyValues()));

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
                message.setStringProperty(MsgPropertyName.GroupId, msg.groupId());

                /*
                 * Framework headers
                 */
                // message.setStringProperty(MsgPropertyName.Invoking, msg.getInvoking());
                // message.setBooleanProperty(MsgPropertyName.ServerThrown, msg.isException());

                try (final MessageProducer producer = session.createProducer(null)) {

                    producer.send(message);

                    session.commit();

                    LOGGER.trace("Sent {} ", msg.correlationId());

                    return message;
                }
            } catch (final JMSException e) {
                LOGGER.error("Failed to take: to {}, type {}, correclation id {}", destination.toString(), msg.type(),
                        msg.correlationId(), e);
                throw new MsgFnException(e);
            }
        };
    }
}
