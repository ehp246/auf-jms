package me.ehp246.aufjms.api.dispatch;

import javax.jms.Connection;
import javax.jms.JMSProducer;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * The abstraction
 * 
 * import me.ehp246.aufjms.api.jms.JmsMsg; of a JMS {@link JMSProducer}. The
 * producer is scoped to a {@link Connection}.
 * <p>
 * The interface only is concerned with sending the message.
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface DispatchFn {
    JmsMsg dispatch(JmsDispatch dispatch);
}
