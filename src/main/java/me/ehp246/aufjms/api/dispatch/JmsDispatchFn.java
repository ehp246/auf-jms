package me.ehp246.aufjms.api.dispatch;

import jakarta.jms.Connection;
import jakarta.jms.JMSProducer;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * The abstraction of a JMS {@link JMSProducer}. The producer is scoped to a
 * {@link Connection}.
 * <p>
 * The interface only is concerned with sending the message.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface JmsDispatchFn {
    JmsMsg send(JmsDispatch dispatch);
}
