package me.ehp246.aufjms.api.dispatch;

import jakarta.jms.JMSProducer;
import me.ehp246.aufjms.api.exception.JmsDispatchFailedException;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * The abstraction of a JMS {@link JMSProducer}.
 * <p>
 * The interface is concerned only with sending the message.
 *
 * @author Lei Yang
 * @see JmsDispatchFailedException
 */
@FunctionalInterface
public interface JmsDispatchFn {
    /**
     *
     * @return The message that has been sent successfully
     * @throws JmsDispatchFailedException If the send operation can't be completed
     *                              successfully.
     */
    JmsMsg send(JmsDispatch dispatch);
}
