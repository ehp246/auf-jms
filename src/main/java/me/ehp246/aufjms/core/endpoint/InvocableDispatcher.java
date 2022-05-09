package me.ehp246.aufjms.core.endpoint;

import java.util.concurrent.Executor;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.BoundInvoker;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.endpoint.InvocationListener.OnFailed;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class InvocableDispatcher {
    private static final Logger LOGGER = LogManager.getLogger(InboundMsgConsumer.class);

    private final Executor executor;
    private final InvocableBinder binder;
    private final BoundInvoker invoker;
    private final JmsDispatchFn dispatchFn;
    private final InvocationListener listener;

    InvocableDispatcher(@Nullable final Executor executor, final InvocableBinder binder,
            final BoundInvoker invoker, final JmsDispatchFn dispatchFn,
            @Nullable final InvocationListener listener) {
        super();
        this.binder = binder;
        this.executor = executor;
        this.dispatchFn = dispatchFn;
        this.invoker = invoker;
        this.listener = listener;
    }

    public void dispatch(final Invocable invocable, final JmsMsg msg, final Session session) {
        final var runnable = newRunnable(msg, invocable);

        if (executor == null || invocable.invocationModel() == InvocationModel.INLINE) {

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
                        if (!(listener instanceof OnFailed failedListener)) {
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

                    if (listener instanceof OnCompleted completedListener) {
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

                    InvocableDispatcher.this.dispatchFn.send(JmsDispatch.toDispatch(toAt(replyTo), msg.type(),
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
