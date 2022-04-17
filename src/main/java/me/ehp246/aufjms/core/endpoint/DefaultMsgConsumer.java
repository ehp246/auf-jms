package me.ehp246.aufjms.core.endpoint;

import java.util.concurrent.Executor;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.listener.SessionAwareMessageListener;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultMsgConsumer implements SessionAwareMessageListener<Message> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultMsgConsumer.class);

    private final Executor executor;
    private final ExecutableResolver executableResolver;
    private final ExecutableBinder binder;
    private final JmsDispatchFn dispatchFn;
    private final InvocationListenersSupplier invocationListener;

    DefaultMsgConsumer(final ExecutableResolver executableResolver, final ExecutableBinder binder,
            final Executor executor, final JmsDispatchFn dispatchFn,
            final InvocationListenersSupplier invocationListener) {
        super();
        this.executableResolver = executableResolver;
        this.binder = binder;
        this.executor = executor;
        this.dispatchFn = dispatchFn;
        this.invocationListener = invocationListener;
    }

    @Override
    public void onMessage(final Message message, final Session session) throws JMSException {
        if (!(message instanceof TextMessage textMessage)) {
            throw new RuntimeException("Un-supported message type of " + message.getJMSCorrelationID());
        }
        final var msg = TextJmsMsg.from(textMessage);

        // Make sure the thread context is cleaned up.
        try {
            AufJmsContext.set(session);

            Log4jContext.set(msg);

            LOGGER.atTrace().log("Consuming");

            dispatch(msg, session);

            LOGGER.atTrace().log("Consumed");
        } catch (Exception e) {
            LOGGER.atError().log("Message failed: {}", e.getMessage());
            throw e;
        } finally {
            Log4jContext.clear();

            AufJmsContext.clearSession();
        }
    }

    private void dispatch(final JmsMsg msg, final Session session) {
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
                    AufJmsContext.set(session);
                    Log4jContext.set(msg);

                    runnable.run();

                } finally {
                    Log4jContext.clear();
                    AufJmsContext.clearSession();
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
        return new Runnable() {
            @Override
            public void run() {
                final var executionOutcome = binder.bind(target, () -> msg).get();

                final var thrown = executionOutcome.thrown();

                if (thrown != null) {
                    if (invocationListener.failedInterceptor() != null) {
                        try {
                            invocationListener.failedInterceptor()
                                    .accept(new FailedInvocationRecord(msg, target, thrown));
                            LOGGER.atTrace().log("Failure interceptor invoked");
                        } catch (Exception e) {
                            LOGGER.atTrace().log("Failure interceptor failed: {}", e::getMessage);
                            throw e;
                        }
                        return;
                    }

                    if (thrown instanceof RuntimeException rtEx) {
                        throw rtEx;
                    } else {
                        throw new RuntimeException(thrown);
                    }
                } else {
                    if (invocationListener.completedConsumer() != null) {
                        try {
                            invocationListener.completedConsumer()
                                    .accept(new CompletedInvocationRecord(msg, target, executionOutcome.returned()));
                        } catch (Exception e) {
                            // Do not re-throw. Defined by the consumer API.
                            LOGGER.atError().log("Completed consumer failed: {}", e.getMessage(), e);
                        }
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

                DefaultMsgConsumer.this.dispatchFn.send(new JmsDispatch() {
                    private final At to = from(replyTo);

                    @Override
                    public At to() {
                        return to;
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
                    public Object body() {
                        return executionOutcome.returned();
                    }
                });
            }
        };
    }

    private static At from(final Destination replyTo) {
        try {
            return replyTo instanceof Queue ? At.toQueue(((Queue) replyTo).getQueueName())
                    : At.toTopic(((Topic) replyTo).getTopicName());
        } catch (JMSException e) {
            throw new JMSRuntimeException(e.getMessage(), e.getErrorCode(), e);
        }
    }

}
