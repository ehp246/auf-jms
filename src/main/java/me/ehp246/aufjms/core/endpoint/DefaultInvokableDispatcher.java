package me.ehp246.aufjms.core.endpoint;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.InvokableDispatcher;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.reflection.CatchingInvocation;
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
    public void dispatch(final JmsMsg msg) {
        LOGGER.atTrace().log("Dispatching");

        final var resolveOutcome = CatchingInvocation.invoke(() -> this.executableResolver.resolve(msg));
        if (resolveOutcome.hasThrown()) {
            LOGGER.atError().log("Resolution failed", resolveOutcome.getThrown());
            return;
        }

        final var target = resolveOutcome.getReturned();
        if (target == null) {
            LOGGER.atInfo().log("Un-matched message {} {}", msg.type(), msg.correlationId());
            return;
        }

        LOGGER.atTrace().log("Submitting {}", target.getMethod());

        final var runnable = newRunnable(msg, target, binder);

        if (executor == null
                || (target.getInvocationModel() != null && target.getInvocationModel() == InvocationModel.INLINE)) {
            LOGGER.atTrace().log("Executing");

            final var thrown = runnable.invoke().getThrown();

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

                runnable.invoke();

                LOGGER.atTrace().log("Executed");
                ThreadContext.remove(AufJmsProperties.MSG_TYPE);
                ThreadContext.remove(AufJmsProperties.CORRELATION_ID);
            });
        }
    };

    private static CatchingInvocation newRunnable(final JmsMsg msg, final Executable target,
            final ExecutableBinder binder) {
        return () -> {
            final var bindOutcome = CatchingInvocation.invoke(() -> binder.bind(target, () -> msg));
            final var outcome = bindOutcome.ifReturnedPresent().map(CatchingInvocation::invoke)
                    .orElseGet(() -> InvocationOutcome.thrown(bindOutcome.getThrown()));

            final var postEexcution = target.postExecution();
            if (postEexcution == null) {
                return outcome;
            }

            LOGGER.atTrace().log("Executing postExecution");

            postEexcution.accept(new ExecutedInstance() {

                @Override
                public InvocationOutcome<?> getOutcome() {
                    return outcome;
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

            LOGGER.atTrace().log("Executed postExecution");
            return outcome;
        };
    }
}
