package me.ehp246.aufjms.core.byjms;

import java.util.Objects;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.ToJson;
import me.ehp246.aufjms.api.exception.DispatchFnException;
import me.ehp246.aufjms.api.jms.DispatchFn;
import me.ehp246.aufjms.api.jms.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.MsgPropertyName;
import me.ehp246.aufjms.api.jms.NamedConnectionProvider;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultDispatchFnProvider implements JmsDispatchFnProvider {
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
        return dispatch -> {
            LOGGER.atTrace().log("Sending {}:{} to {} ", dispatch.correlationId(), dispatch.type(),
                    dispatch.destination().toString());

            try (final Session session = connProvider.get(connectionName).createSession(true, Session.SESSION_TRANSACTED)) {
                final var message = session.createTextMessage();
                message.setText(this.toJson.toJson(dispatch.bodyValues()));

                // Fill the customs first so the framework ones won't get over-written.
//                final var map = Optional.ofNullable(msg.getPropertyMap()).orElseGet(HashMap<String, String>::new);
//                for (final String key : map.keySet()) {
//                    message.setStringProperty(key, map.get(key));
//                }
                /*
                 * JMS headers
                 */
                message.setJMSReplyTo(dispatch.replyTo());
                message.setJMSType(dispatch.type());
                message.setJMSCorrelationID(dispatch.correlationId());
                message.setStringProperty(MsgPropertyName.GROUP_ID, dispatch.groupId());
                message.setIntProperty(MsgPropertyName.GROUP_SEQ, dispatch.groupSeq());

                /*
                 * Framework headers
                 */
                // message.setStringProperty(MsgPropertyName.Invoking, msg.getInvoking());
                // message.setBooleanProperty(MsgPropertyName.ServerThrown, msg.isException());

                message.setText(toJson.toJson(dispatch.bodyValues()));

                try (final MessageProducer producer = session.createProducer(null)) {

                    producer.setTimeToLive(Optional.ofNullable(dispatch.ttl()).orElse((long) 0));

                    producer.send(dispatch.destination(), message);

                    session.commit();

                    LOGGER.atTrace().log("Sent {} ", dispatch.correlationId());

                    return message;
                }
            } catch (final JMSException e) {
                LOGGER.atError().log("Failed to send: to {}, type {}, correclation id {}",
                        dispatch.destination().toString(), dispatch.type(), dispatch.correlationId(), e);
                throw new DispatchFnException(e);
            }
        };
    }
}
