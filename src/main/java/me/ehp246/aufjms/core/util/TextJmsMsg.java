package me.ehp246.aufjms.core.util;

import java.time.Instant;
import java.util.concurrent.Callable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.exception.JmsException;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.MsgPropertyName;

/**
 * Utility to un-pack a JMS message.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final class TextJmsMsg implements JmsMsg {
    private final TextMessage message;

    private TextJmsMsg(final TextMessage message) {
        super();
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
    public String correlationId() {
        return invoke(message::getJMSCorrelationID);
    }

    @Override
    public Destination replyTo() {
        return invoke(message::getJMSReplyTo);
    }

    @Override
    public String groupId() {
        return invoke(() -> message.getStringProperty(MsgPropertyName.GROUP_ID));
    }

    @Override
    public long expiration() {
        return invoke(message::getJMSExpiration);
    }

    @Override
    public Destination destination() {
        return invoke(message::getJMSDestination);
    }

    @Override
    public String invoking() {
        return null;
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
    public Instant timestamp() {
        return Instant.ofEpochMilli(invoke(message::getJMSTimestamp));
    }

    @Override
    public TextMessage message() {
        return message;
    }

    public static JmsMsg from(final TextMessage message) {
        return new TextJmsMsg(message);
    }

    private static <V> V invoke(Callable<V> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (e instanceof JMSException) {
                throw new JmsException((JMSException) e);
            }
            throw new RuntimeException(e);
        }
    }
}
