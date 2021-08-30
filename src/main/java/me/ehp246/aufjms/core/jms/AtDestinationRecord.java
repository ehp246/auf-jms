package me.ehp246.aufjms.core.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSSecurityRuntimeException;
import javax.jms.Session;

import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 * @since 1.0
 */
public class AtDestinationRecord implements AtDestination {
    private final String name;
    private final DestinationType type;

    public AtDestinationRecord(final AtDestination at) {
        super();
        this.name = at.name();
        this.type = at.type();
    }

    public AtDestinationRecord(final String name, final DestinationType type) {
        super();
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public DestinationType type() {
        return this.type;
    }

    @Override
    public String toString() {
        return (this.type() == DestinationType.QUEUE ? "queue://" : "topic://") + this.name;
    }

    public Destination jmsDestination(Session session) {
        try {
            return type == DestinationType.QUEUE ? session.createQueue(name) : session.createTopic(name);
        } catch (JMSException e) {
            throw new JMSSecurityRuntimeException(e.getMessage(), e.getErrorCode(), e);
        }
    }
}
