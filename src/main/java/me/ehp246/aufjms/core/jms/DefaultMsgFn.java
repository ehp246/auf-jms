package me.ehp246.aufjms.core.jms;

import java.util.HashMap;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.jms.Msg;
import me.ehp246.aufjms.api.jms.MsgFn;
import me.ehp246.aufjms.api.jms.MsgPropertyName;
import me.ehp246.aufjms.core.util.ToMsg;

/**
 * @author Lei Yang
 *
 */
public final class DefaultMsgFn implements MsgFn {
    private final static Logger LOGGER = LogManager.getLogger(DefaultMsgFn.class);

    @Override
    public Message apply(Msg msg) {
        final var destination = msg.destination();

        LOGGER.trace("Sending {}:{} to {} ", msg.correlationId(), msg.type(),
                destination.toString());

        try (final Session session = connection.createSession(true, Session.SESSION_TRANSACTED)) {

            final Message message = null;

            // Fill the customs first so the framework ones won't get over-written.
            final var map = Optional.ofNullable(msg.propertyMap()).orElseGet(HashMap<String, String>::new);
            for (final String key : map.keySet()) {
                message.setStringProperty(key, map.get(key));
            }

            message.setJMSDestination(supplier.getTo());
            message.setJMSReplyTo(supplier.getReplyTo());

            /*
             * JMS headers
             */
            message.setJMSType(msgSupplier.getType());
            message.setJMSCorrelationID(msgSupplier.getCorrelationId());
            message.setStringProperty(MsgPropertyName.GroupId, msgSupplier.getGroupId());

            /*
             * Framework headers
             */
            message.setStringProperty(MsgPropertyName.Invoking, msgSupplier.getInvoking());
            message.setBooleanProperty(MsgPropertyName.ServerThrown, msgSupplier.isException());

            try (final MessageProducer producer = session.createProducer(destination)) {

                producer.send(message);

                session.commit();

                LOGGER.trace("Sent {} ", msgSupplier.getCorrelationId());

                return ToMsg.from(message);
            }
        } catch (final JMSException e) {
            LOGGER.error("Failed to take: to {}, type {}, correclation id {}", destination.toString(),
                    msgSupplier.getType(), msgSupplier.getCorrelationId(), e);
            throw new RuntimeException(e);
        }
        return null;
    }

}
