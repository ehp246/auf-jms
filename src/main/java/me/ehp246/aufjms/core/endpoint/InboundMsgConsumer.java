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
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
final class InboundMsgConsumer implements SessionAwareMessageListener<Message> {
    private static final Logger LOGGER = LogManager.getLogger(InboundMsgConsumer.class);

    private final Executor executor;
    private final ExecutableResolver executableResolver;
    private final ExecutableBinder binder;
    private final JmsDispatchFn dispatchFn;
    private final InvocationListenersSupplier invocationListener;

    InboundMsgConsumer(final ExecutableResolver executableResolver, final ExecutableBinder binder,
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
            throw new IllegalArgumentException("Un-supported message type of " + message.getJMSCorrelationID());
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
                    if (invocationListener.failedInterceptor() == null) {
                        throw OneUtil.toRuntime(thrown);
                    }

                    LOGGER.atTrace().log("Executing failed interceptor");
                    try {
                        invocationListener.failedInterceptor().accept(new FailedInvocationRecord(msg, target, thrown));
                        LOGGER.atTrace().log("Failure interceptor invoked");
                        /*
                         * Skip further execution on invocation exception but acknowledge the message.
                         */
                        return;
                    } catch (Exception e) {
                        LOGGER.atTrace().log("Failure interceptor failed: {}", e::getMessage);

                        throw OneUtil.toRuntime(e);
                    }
                }

                if (invocationListener.completedConsumer() != null) {
                    LOGGER.atTrace().log("Executing completed consumer");
                    try {
                        invocationListener.completedConsumer()
                                .accept(new CompletedInvocationRecord(msg, target, executionOutcome.returned()));
                        LOGGER.atTrace().log("Completed consumer invoked");
                    } catch (Exception e) {
                        LOGGER.atTrace().log("Completed consumer failed: {}", e.getMessage());

                        throw OneUtil.toRuntime(e);
                    }
                }

                // Reply
                final var replyTo = msg.replyTo();
                if (replyTo == null) {
                    LOGGER.atTrace().log("No replyTo");
                    return;
                }

                LOGGER.atTrace().log("Replying");

                InboundMsgConsumer.this.dispatchFn.send(JmsDispatch.toDispatch(toAt(replyTo), msg.type(),
                        executionOutcome.returned(), msg.correlationId()));

                LOGGER.atTrace().log("Replied");
            }
        };
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
