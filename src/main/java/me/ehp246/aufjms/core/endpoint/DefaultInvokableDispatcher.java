package me.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

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
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.InvokableDispatcher;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.jms.AtDestinationRecord;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultInvokableDispatcher implements InvokableDispatcher, SessionAwareMessageListener<Message> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultInvokableDispatcher.class);

    private final Executor executor;
    private final ExecutableResolver executableResolver;
    private final ExecutableBinder binder;
    private final JmsDispatchFn dispatchFn;

    DefaultInvokableDispatcher(final ExecutableResolver executableResolver, final ExecutableBinder binder,
            final Executor executor, final JmsDispatchFn dispatchFn) {
        super();
        this.executableResolver = executableResolver;
        this.binder = binder;
        this.executor = executor;
        this.dispatchFn = dispatchFn;
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        if (!(message instanceof TextMessage)) {
            throw new RuntimeException("Un-supported Message type: " + message.getClass().getSimpleName());
        }

        // Make sure the thread context is cleaned up.
        try {
            AufJmsContext.set(session);

            ThreadContext.put(AufJmsProperties.TYPE, message.getJMSType());
            ThreadContext.put(AufJmsProperties.CORRELATION_ID, message.getJMSCorrelationID());

            LOGGER.atTrace().log("Dispatching");

            dispatch(TextJmsMsg.from((TextMessage) message));

            // Only when no exception.
            LOGGER.atTrace().log("Dispatched");
        } finally {
            ThreadContext.remove(AufJmsProperties.TYPE);
            ThreadContext.remove(AufJmsProperties.CORRELATION_ID);

            AufJmsContext.clearSession();
        }
    }

    @Override
    public void dispatch(final JmsMsg msg) {
        LOGGER.atTrace().log("Resolving type");

        final var resolveOutcome = InvocationOutcome.invoke(() -> this.executableResolver.resolve(msg));

        if (resolveOutcome.hasThrown()) {
            LOGGER.atError().log("Resolution failed {}", () -> resolveOutcome.getThrown().getMessage());
            final var ex = resolveOutcome.getThrown();
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

        final var target = resolveOutcome.getReturned();
        if (target == null) {
            LOGGER.atError().log("Unknown type");
            throw new UnknownTypeException(msg);
        }

        LOGGER.atTrace().log("Submitting {}", () -> target.getMethod().toString());

        final var outcomeSupplier = newSupplier(msg, target);

        if (executor == null
                || (target.getInvocationModel() != null && target.getInvocationModel() == InvocationModel.INLINE)) {
            LOGGER.atTrace().log("Executing");

            final var thrown = outcomeSupplier.get().getThrown();

            if (thrown != null) {
                if (thrown instanceof RuntimeException) {
                    throw (RuntimeException) thrown;
                }
                throw new RuntimeException(thrown);
            }

            LOGGER.atTrace().log("Executed");
        } else {
            executor.execute(() -> {
                ThreadContext.put(AufJmsProperties.TYPE, msg.type());
                ThreadContext.put(AufJmsProperties.CORRELATION_ID, msg.correlationId());
                LOGGER.atTrace().log("Executing");

                outcomeSupplier.get();

                LOGGER.atTrace().log("Executed");
                ThreadContext.remove(AufJmsProperties.TYPE);
                ThreadContext.remove(AufJmsProperties.CORRELATION_ID);
            });
        }
    };

    private Supplier<InvocationOutcome<?>> newSupplier(final JmsMsg msg, final Executable target) {
        return () -> {
            final var bindingOutcome = InvocationOutcome.invoke(() -> binder.bind(target, () -> msg));

            final var executionOutcome = bindingOutcome.optionalReturned().map(Supplier::get)
                    .orElseGet(() -> InvocationOutcome.thrown(bindingOutcome.getThrown()));

            Optional.ofNullable(target.executionConsumer()).ifPresent(postExecution -> {
                LOGGER.atTrace().log("Executing execution consumer");

                postExecution.accept(new ExecutedInstance() {

                    @Override
                    public InvocationOutcome<?> getOutcome() {
                        return executionOutcome;
                    }

                    @Override
                    public JmsMsg getMsg() {
                        return msg;
                    }

                    @Override
                    public Executable getInstance() {
                        return target;
                    }
                });

                LOGGER.atTrace().log("Executed execution consumer");
            });

            // Reply
            final var replyTo = msg.replyTo();
            if (replyTo == null) {
                LOGGER.atTrace().log("No replyTo");
                return executionOutcome;
            }

            if (executionOutcome.hasThrown()) {
                LOGGER.atTrace().log("Execution thrown, skipping reply");
                return executionOutcome;
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

            return executionOutcome;
        };
    }
}
