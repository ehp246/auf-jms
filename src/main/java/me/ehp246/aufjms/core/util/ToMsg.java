package me.ehp246.aufjms.core.util;

import java.time.Instant;
import java.util.concurrent.Callable;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.jms.MsgPropertyName;
import me.ehp246.aufjms.api.jms.Received;

/**
 * Utility to un-pack a JMS message.
 * 
 * @author Lei Yang
 *
 */
final public class ToMsg {
    private static final class MsgImplementation implements Received {
        private final Message message;

        private MsgImplementation(Message message) {
            this.message = message;
        }

        @Override
        public String id() {
            return invoke(message::getJMSMessageID);
        }

        @Override
        public String type() {
            return invoke(message::getJMSType);
        }

        @Override
        public String getInvoking() {
            return invoke(() -> message.getStringProperty(MsgPropertyName.Invoking));
        }

        @Override
        public String correlationId() {
            return invoke(message::getJMSCorrelationID);
        }

        @Override
        public Destination replyTo() {
            return invoke(message::getJMSReplyTo);
        }

        @Override
        public String groupId() {
            return invoke(() -> message.getStringProperty(MsgPropertyName.GroupId));
        }

        @Override
        public Integer groupSeq() {
            return invoke(() -> message.getIntProperty(MsgPropertyName.GroupSeq));
        }

        @Override
        public boolean isException() {
            return invoke(() -> message.getBooleanProperty(MsgPropertyName.ServerThrown));
        }

        @Override
        public long expiration() {
            return invoke(message::getJMSExpiration);
        }

        @Override
        public Destination destination() {
            return invoke(message::getJMSDestination);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name, Class<T> type) {
            if (type == String.class) {
                return (T) invoke(() -> message.getStringProperty(name));
            }
            if (type == int.class) {
                return (T) invoke(() -> message.getIntProperty(name));
            }
            if (type == long.class) {
                return (T) invoke(() -> message.getLongProperty(name));
            }
            if (type == boolean.class) {
                return (T) invoke(() -> message.getBooleanProperty(name));
            }

            throw new RuntimeException("Un-supported property type " + type.getTypeName());
        }

        @Override
        public long ttl() {
            return invoke(() -> message.getIntProperty(MsgPropertyName.TTL));
        }

        @Override
        public Instant timestamp() {
            return Instant.ofEpochMilli(invoke(message::getJMSTimestamp));
        }

        @Override
        public Message message() {
            return message;
        }
    }

    private ToMsg() {
        super();
    }

    public static Received from(final Message message) {
        if (!(message instanceof TextMessage)) {
            throw new RuntimeException("Un-supported message type: " + message.getClass().getName());
        }

        return new MsgImplementation(message);
    }

    private static <V> V invoke(Callable<V> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
