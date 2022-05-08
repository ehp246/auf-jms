package me.ehp246.aufjms.core.endpoint;

import java.util.Optional;
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
import me.ehp246.aufjms.api.endpoint.CompletedInvocation;
import me.ehp246.aufjms.api.endpoint.FailedInvocation;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.InvocableFactory;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.ToInvoke;
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
    private final InvocableFactory factory;
    private final InvocableBinder binder;
    private final ToInvoke invoker;
    private final JmsDispatchFn dispatchFn;
    private final InvocationListenersSupplier listenerSupplier;

    InboundMsgConsumer(final InvocableFactory factory, final InvocableBinder binder, final ToInvoke invoker,
            final Executor executor, final JmsDispatchFn dispatchFn,
            final InvocationListenersSupplier listenerSupplier) {
        super();
        this.factory = factory;
        this.binder = binder;
        this.executor = executor;
        this.dispatchFn = dispatchFn;
        this.listenerSupplier = listenerSupplier;
        this.invoker = invoker;
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
            LOGGER.atError().withThrowable(e).log("Message failed: {}", e.getMessage());

            throw e;
        } finally {
            Log4jContext.clear();

            AufJmsContext.clearSession();
        }
    }

    private void dispatch(final JmsMsg msg, final Session session) {
        LOGGER.atTrace().log("Resolving {}", msg::type);

        final var inovcable = factory.resolve(msg);

        if (inovcable == null) {
            throw new UnknownTypeException(msg);
        }

        LOGGER.atTrace().log("Submitting {}", () -> inovcable.method().toString());

        final var runnable = newRunnable(msg, inovcable);

        if (executor == null || inovcable.invocationModel() == InvocationModel.INLINE) {

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
    private Runnable newRunnable(final JmsMsg msg, final Invocable target) {
        return new Runnable() {
            @Override
            public void run() {
                final var bound = binder.bind(target, () -> msg);
                final var outcome = invoker.apply(bound);

                Optional.ofNullable(target.closeable()).ifPresent(closeable -> {
                    try (closeable) {
                    } catch (Exception e) {
                        LOGGER.atError().withThrowable(e).log("Close failed, ignored: {}", e::getMessage);
                    }
                });

                if (outcome instanceof FailedInvocation failed) {
                    if (listenerSupplier.failedInterceptor() == null) {
                        throw OneUtil.ensureRuntime(failed.thrown());
                    }

                    LOGGER.atTrace().log("Executing failed interceptor");
                    try {
                        listenerSupplier.failedInterceptor().accept(failed);
                        LOGGER.atTrace().log("Failure interceptor invoked");
                        /*
                         * Skip further execution on invocation exception but acknowledge the message.
                         */
                        return;
                    } catch (Exception e) {
                        LOGGER.atTrace().withThrowable(e).log("Failure interceptor threw: {}", e::getMessage);

                        throw OneUtil.ensureRuntime(e);
                    }
                }

                assert (outcome instanceof CompletedInvocation);

                final var completed = (CompletedInvocation) outcome;

                if (listenerSupplier.completedListener() != null) {
                    LOGGER.atTrace().log("Executing completed consumer");

                    try {
                        listenerSupplier.completedListener().accept(completed);

                        LOGGER.atTrace().log("Completed consumer invoked");
                    } catch (Exception e) {
                        LOGGER.atTrace().withThrowable(e).log("Completed consumer failed: {}", e.getMessage());

                        throw OneUtil.ensureRuntime(e);
                    }
                }

                // Reply
                final var replyTo = msg.replyTo();
                if (replyTo == null) {
                    return;
                }

                LOGGER.atTrace().log("Replying to {}", replyTo);

                InboundMsgConsumer.this.dispatchFn.send(
                        JmsDispatch.toDispatch(toAt(replyTo), msg.type(), completed.returned(), msg.correlationId()));
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
