package me.ehp246.aufjms.core.endpoint;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.InvokableDispatcher;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvokableDispatcher implements InvokableDispatcher {
    private static final Logger LOGGER = LogManager.getLogger(DefaultInvokableDispatcher.class);

    private final Executor executor;
    private final ExecutableResolver executableResolver;
    private final ExecutableBinder binder;

    public DefaultInvokableDispatcher(final ExecutableResolver executableResolver, final ExecutableBinder binder,
            final Executor executor) {
        super();
        this.executableResolver = executableResolver;
        this.binder = binder;
        this.executor = executor;
    }

    @Override
    public void dispatch(final MsgContext msgCtx) {
        final var msg = msgCtx.msg();

        LOGGER.atTrace().log("Dispatching");

        final var resolveOutcome = InvocationOutcome.invoke(() -> this.executableResolver.resolve(msg));
        if (resolveOutcome.hasThrown()) {
            LOGGER.atError().log("Resolution failed", resolveOutcome.getThrown().getMessage());
            final var ex = resolveOutcome.getThrown();
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

        final var target = resolveOutcome.getReturned();
        if (target == null) {
            LOGGER.atInfo().log("Un-matched message {} {}", msg.type(), msg.correlationId());
            return;
        }

        LOGGER.atTrace().log("Submitting {}", target.getMethod());

        final var runnable = newInvocation(msgCtx, target, binder);

        if (executor == null
                || (target.getInvocationModel() != null && target.getInvocationModel() == InvocationModel.INLINE)) {
            LOGGER.atTrace().log("Executing");

            final var thrown = runnable.get().getThrown();

            if (thrown != null) {
                if (thrown instanceof RuntimeException) {
                    throw (RuntimeException) thrown;
                }
                throw new RuntimeException(thrown);
            }

            LOGGER.atTrace().log("Executed");
        } else {
            executor.execute(() -> {
                ThreadContext.put(AufJmsProperties.MSG_TYPE, msg.type());
                ThreadContext.put(AufJmsProperties.CORRELATION_ID, msg.correlationId());
                LOGGER.atTrace().log("Executing");

                runnable.get();

                LOGGER.atTrace().log("Executed");
                ThreadContext.remove(AufJmsProperties.MSG_TYPE);
                ThreadContext.remove(AufJmsProperties.CORRELATION_ID);
            });
        }
    };

    private static Supplier<InvocationOutcome<?>> newInvocation(final MsgContext msgCtx, final Executable target,
            final ExecutableBinder binder) {
        return () -> {
            final var bindingOutcome = InvocationOutcome.invoke(() -> binder.bind(target, msgCtx));

            final var executionOutcome = bindingOutcome.optionalReturned().map(Supplier::get)
                    .orElseGet(() -> InvocationOutcome.thrown(bindingOutcome.getThrown()));

            final var postEexcution = target.postExecution();
            if (postEexcution == null) {
                return executionOutcome;
            }

            LOGGER.atTrace().log("Executing postExecution");

            postEexcution.accept(new ExecutedInstance() {

                @Override
                public InvocationOutcome<?> getOutcome() {
                    return executionOutcome;
                }

                @Override
                public JmsMsg getMsg() {
                    return msgCtx.msg();
                }

                @Override
                public Executable getInstance() {
                    return target;
                }
            });

            LOGGER.atTrace().log("Executed postExecution");
            return executionOutcome;
        };
    }
}
