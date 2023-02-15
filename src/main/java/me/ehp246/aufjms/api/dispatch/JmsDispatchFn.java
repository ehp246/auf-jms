package me.ehp246.aufjms.api.dispatch;

import jakarta.jms.JMSProducer;
import me.ehp246.aufjms.api.exception.JmsDispatchException;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * The abstraction of a JMS {@link JMSProducer}.
 * <p>
 * The interface is concerned only with sending the message.
 *
 * @author Lei Yang
 * @see JmsDispatchException
 */
@FunctionalInterface
public interface JmsDispatchFn {
    /**
     *
     * @return The message that has been sent successfully
     * @throws JmsDispatchException If the send operation can't be completed
     *                              successfully.
     */
    JmsMsg send(JmsDispatch dispatch);
}
