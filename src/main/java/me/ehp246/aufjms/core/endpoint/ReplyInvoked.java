package me.ehp246.aufjms.core.endpoint;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.jms.At;

final class ReplyInvoked implements InvocationListener.OnCompleted {
    private static final Logger LOGGER = LogManager.getLogger(ReplyInvoked.class);

    private final JmsDispatchFn dispatchFn;

    ReplyInvoked(JmsDispatchFn dispatchFn) {
        this.dispatchFn = dispatchFn;
    }

    @Override
    public void onCompleted(Completed completed) {
        final var msg = completed.bound().msg();
        final var replyTo = msg.replyTo();
        if (replyTo == null) {
            return;
        }

        LOGGER.atTrace().log("Replying to {}", replyTo);

        dispatchFn.send(JmsDispatch.toDispatch(toAt(replyTo), msg.type(), completed.returned(),
                msg.correlationId()));
    }

    private static At toAt(final Destination replyTo) {
        try {
            return replyTo instanceof Queue ? At.toQueue(((Queue) replyTo).getQueueName())
                    : At.toTopic(((Topic) replyTo).getTopicName());
        } catch (JMSException e) {
            throw new JMSRuntimeException(e.getMessage(), e.getErrorCode(), e);
        }
    }
}