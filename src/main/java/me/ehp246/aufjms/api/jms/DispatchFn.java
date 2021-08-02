package me.ehp246.aufjms.api.jms;

import javax.jms.Connection;
import javax.jms.JMSProducer;
import javax.jms.Message;

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
public interface DispatchFn {
    Message apply(JmsDispatch msg);
}
