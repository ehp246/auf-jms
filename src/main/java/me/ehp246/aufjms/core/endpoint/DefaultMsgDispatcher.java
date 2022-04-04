package me.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.concurrent.Executor;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.jms.listener.SessionAwareMessageListener;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.FailedMsgConsumer;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.jms.AtDestinationRecord;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultMsgDispatcher implements SessionAwareMessageListener<Message> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultMsgDispatcher.class);

    private final Executor executor;
    private final ExecutableResolver executableResolver;
    private final ExecutableBinder binder;
    private final JmsDispatchFn dispatchFn;
    private final FailedMsgConsumer failedMsgConsumer;

    DefaultMsgDispatcher(final ExecutableResolver executableResolver, final ExecutableBinder binder,
            final Executor executor, final JmsDispatchFn dispatchFn, final FailedMsgConsumer failedMsgConsumer) {
        super();
        this.executableResolver = executableResolver;
        this.binder = binder;
        this.executor = executor;
        this.dispatchFn = dispatchFn;
        this.failedMsgConsumer = failedMsgConsumer;
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        if (message instanceof TextMessage textMessage) {
            // Make sure the thread context is cleaned up.
            try {
                AufJmsContext.set(session);

                ThreadContext.put(AufJmsProperties.TYPE, message.getJMSType());
                ThreadContext.put(AufJmsProperties.CORRELATION_ID, message.getJMSCorrelationID());

                LOGGER.atTrace().log("Dispatching");

                dispatch(textMessage);

                // Only when no exception.
                LOGGER.atTrace().log("Dispatched");
            } catch (Exception e) {
                LOGGER.atTrace().log("Dispatch failed");
            } finally {
                ThreadContext.remove(AufJmsProperties.TYPE);
                ThreadContext.remove(AufJmsProperties.CORRELATION_ID);

                AufJmsContext.clearSession();
            }
            return;
        }

        throw new RuntimeException("Un-supported Message: " + message.getJMSCorrelationID());
    }

    private void dispatch(final TextMessage message) {
        final var msg = TextJmsMsg.from(message);

        LOGGER.atTrace().log("Resolving executable");

        final var executable = executableResolver.resolve(msg);

        if (executable == null) {
            throw new UnknownTypeException(msg);
        }

        LOGGER.atTrace().log("Submitting {}", () -> executable.method().toString());

        final var runnable = newRunnable(msg, executable);

        if (executor == null || executable.invocationModel() == InvocationModel.INLINE) {

            runnable.run();

        } else {
            executor.execute(() -> {
                try {
                    ThreadContext.put(AufJmsProperties.TYPE, msg.type());
                    ThreadContext.put(AufJmsProperties.CORRELATION_ID, msg.correlationId());

                    runnable.run();

                } finally {
                    ThreadContext.remove(AufJmsProperties.TYPE);
                    ThreadContext.remove(AufJmsProperties.CORRELATION_ID);
                }
            });
        }
    };

    /**
     * The runnable returned is expected to handle all execution and exception. The
     * caller simply invokes this runnable without further processing.
     * 
     * @param msg
     * @param target
     * @return
     */
    private Runnable newRunnable(final JmsMsg msg, final Executable target) {
        return () -> {
            final var executionOutcome = binder.bind(target, () -> msg).get();

            final var thrown = executionOutcome.getThrown();

            if (thrown != null) {
                if (failedMsgConsumer != null) {
                    failedMsgConsumer.accept(new FailedMsgRecord(msg, thrown));
                    return;
                }

                if (thrown instanceof RuntimeException rtEx) {
                    throw rtEx;
                } else {
                    throw new RuntimeException(thrown);
                }
            }

            // Reply
            final var replyTo = msg.replyTo();
            if (replyTo == null) {
                LOGGER.atTrace().log("No replyTo");
                return;
            }

            if (executionOutcome.hasThrown()) {
                LOGGER.atTrace().log("Execution thrown, skipping reply");
                return;
            }

            LOGGER.atTrace().log("Replying");

            this.dispatchFn.send(new JmsDispatch() {
                final List<?> bodyValues = executionOutcome.getReturned() != null
                        ? List.of(executionOutcome.getReturned())
                        : List.of();
                final AtDestination at = AtDestinationRecord.from(replyTo);

                @Override
                public AtDestination at() {
                    return at;
                }

                @Override
                public String type() {
                    return msg.type();
                }

                @Override
                public String correlationId() {
                    return msg.correlationId();
                }

                @Override
                public List<?> bodyValues() {
                    return bodyValues;
                }
            });
        };
    }
}
