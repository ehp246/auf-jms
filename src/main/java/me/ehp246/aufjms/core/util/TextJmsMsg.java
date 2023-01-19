package me.ehp246.aufjms.core.util;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.jms.Destination;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.jms.JMSSupplier;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.JmsNames;

/**
 * Utility to un-pack a JMS message. All read calls are delayed and on-demand.
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
        return JMSSupplier.invoke(message::getJMSMessageID);
    }

    @Override
    public String type() {
        return JMSSupplier.invoke(message::getJMSType);
    }

    @Override
    public String correlationId() {
        return JMSSupplier.invoke(message::getJMSCorrelationID);
    }

    @Override
    public Destination replyTo() {
        return JMSSupplier.invoke(message::getJMSReplyTo);
    }

    @Override
    public String text() {
        return JMSSupplier.invoke(message::getText);
    }

    @Override
    public String groupId() {
        return JMSSupplier.invoke(() -> message.getStringProperty(JmsNames.GROUP_ID));
    }

    @Override
    public int groupSeq() {
        return JMSSupplier.invoke(() -> message.getIntProperty(JmsNames.GROUP_SEQ));
    }

    @Override
    public Instant expiration() {
        return Instant.ofEpochMilli(JMSSupplier.invoke(message::getJMSExpiration));
    }

    @Override
    public int deliveryCount() {
        return JMSSupplier.invoke(() -> message.getIntProperty(JmsNames.DELIVERY_COUNT));
    }

    @Override
    public boolean redelivered() {
        return JMSSupplier.invoke(() -> message.getJMSRedelivered());
    }

    @Override
    public Destination destination() {
        return JMSSupplier.invoke(message::getJMSDestination);
    }

    @Override
    public String invoking() {
        return JMSSupplier.invoke(() -> message.getStringProperty(JmsNames.INVOKING));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> propertyNames() {
        return new HashSet<>(Collections.<String>list(JMSSupplier.invoke(message::getPropertyNames)));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T property(final String name, final Class<T> type) {
        if (type == String.class) {
            return (T) JMSSupplier.invoke(() -> message.getStringProperty(name));
        }
        if (type == int.class || type == Integer.class) {
            return (T) JMSSupplier.invoke(() -> message.getIntProperty(name));
        }
        if (type == long.class || type == Long.class) {
            return (T) JMSSupplier.invoke(() -> message.getLongProperty(name));
        }
        if (type == boolean.class || type == Boolean.class) {
            return (T) JMSSupplier.invoke(() -> message.getBooleanProperty(name));
        }
        if (type.isEnum()) {
            return (T) JMSSupplier
                    .invoke(() -> Enum.valueOf((Class<Enum>) type, message.getStringProperty(name)));
        }

        throw new RuntimeException("Un-supported property type " + type.getTypeName());
    }

    @Override
    public Instant timestamp() {
        return Instant.ofEpochMilli(JMSSupplier.invoke(message::getJMSTimestamp));
    }

    @Override
    public TextMessage message() {
        return message;
    }

    public static JmsMsg from(final TextMessage message) {
        return new TextJmsMsg(message);
    }
}
