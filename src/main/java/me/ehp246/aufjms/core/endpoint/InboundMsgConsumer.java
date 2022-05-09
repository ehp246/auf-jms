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
import org.springframework.lang.Nullable;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.BoundInvoker;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.InvocationListener.CompletedListener;
import me.ehp246.aufjms.api.endpoint.InvocationListener.FailedInterceptor;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.endpoint.MsgInvocableFactory;
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
    private final MsgInvocableFactory factory;
    private final InvocableBinder binder;
    private final BoundInvoker invoker;
    private final JmsDispatchFn dispatchFn;
    private final InvocationListener listener;

    InboundMsgConsumer(final MsgInvocableFactory factory, final InvocableBinder binder, final BoundInvoker invoker,
            @Nullable final Executor executor, final JmsDispatchFn dispatchFn,
            @Nullable final InvocationListener listener) {
        super();
        this.factory = factory;
        this.binder = binder;
        this.executor = executor;
        this.dispatchFn = dispatchFn;
        this.invoker = invoker;
        this.listener = listener;
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

        final var inovcable = factory.get(msg);

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
                try {
                    final var bound = binder.bind(target, () -> msg);

                    assert (bound != null);

                    final var outcome = invoker.apply(bound);

                    assert (outcome != null);

                    if (outcome instanceof Failed failed) {
                        if (!(listener instanceof FailedInterceptor failedListener)) {
                            throw failed.thrown();
                        }

                        LOGGER.atTrace().log("Executing failed interceptor");
                        try {
                            failedListener.onFailed(failed);
                            LOGGER.atTrace().log("Failure interceptor invoked");
                            /*
                             * If the interceptor does not throw, skip further execution and acknowledge the
                             * message.
                             */
                            return;
                        } catch (Exception e) {
                            LOGGER.atTrace().withThrowable(e).log("Failure interceptor threw: {}", e::getMessage);

                            throw e;
                        }
                    }

                    assert (outcome instanceof Completed);

                    final var completed = (Completed) outcome;

                    if (listener instanceof CompletedListener completedListener) {
                        LOGGER.atTrace().log("Executing completed consumer");

                        try {
                            completedListener.onCompleted(completed);

                            LOGGER.atTrace().log("Completed consumer invoked");
                        } catch (Exception e) {
                            LOGGER.atTrace().withThrowable(e).log("Completed consumer failed: {}", e.getMessage());

                            throw e;
                        }
                    }

                    // Reply
                    final var replyTo = msg.replyTo();
                    if (replyTo == null) {
                        return;
                    }

                    LOGGER.atTrace().log("Replying to {}", replyTo);

                    InboundMsgConsumer.this.dispatchFn.send(JmsDispatch.toDispatch(toAt(replyTo), msg.type(),
                            completed.returned(), msg.correlationId()));
                } catch (Throwable e) {
                    throw OneUtil.ensureRuntime(e);
                } finally {
                    try (target) {
                    } catch (Exception e) {
                        LOGGER.atError().withThrowable(e).log("Close failed, ignored: {}", e::getMessage);
                    }
                }
            };
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
