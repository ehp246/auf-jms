package me.ehp246.aufjms.core.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.Queue;
import jakarta.jms.Topic;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.jms.At;

final class ReplyInvoked implements InvocationListener.OnCompleted {
    private static final Logger LOGGER = LogManager.getLogger(ReplyInvoked.class);

    private final JmsDispatchFn dispatchFn;

    ReplyInvoked(final JmsDispatchFn dispatchFn) {
        this.dispatchFn = dispatchFn;
    }

    @Override
    public void onCompleted(final Completed completed) {
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
        } catch (final JMSException e) {
            throw new JMSRuntimeException(e.getMessage(), e.getErrorCode(), e);
        }
    }
}