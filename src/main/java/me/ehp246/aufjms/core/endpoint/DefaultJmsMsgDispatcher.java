package me.ehp246.aufjms.core.endpoint;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Qualifier;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.EndpointDispatcher;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.reflection.CatchingInvocation;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;
import me.ehp246.aufjms.core.reflection.ReflectingInvocation;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultJmsMsgDispatcher implements EndpointDispatcher {
    private static final Logger LOGGER = LogManager.getLogger(DefaultJmsMsgDispatcher.class);

    private final Executor executor;
    private final ExecutableResolver actionResolver;
    private final ExecutableBinder binder;

    public DefaultJmsMsgDispatcher(final ExecutableResolver actionResolver, final ExecutableBinder binder,
            @Qualifier(AufJmsProperties.EXECUTOR_BEAN) final Executor executor) {
        super();
        this.actionResolver = actionResolver;
        this.binder = binder;
        this.executor = executor;
    }

    @Override
    public void dispatch(final JmsMsg msg) {
        LOGGER.atTrace().log("Dispatching");

        final var resolveOutcome = CatchingInvocation.invoke(() -> this.actionResolver.resolve(msg));
        if (resolveOutcome.hasThrown()) {
            LOGGER.atError().log("Resolution failed", resolveOutcome.getThrown());
            return;
        }

        final var target = resolveOutcome.getReturned();
        if (target == null) {
            LOGGER.atInfo().log("Un-matched message {} {}", msg.id(), msg.correlationId());
            return;
        }

        LOGGER.atTrace().log("Submitting");

        final var runnable = newRunnable(msg, target, binder);

        if (target.getInvocationModel() == null || target.getInvocationModel() == InvocationModel.SYNC) {
            LOGGER.atTrace().log("Executing");

            runnable.run();

            LOGGER.atTrace().log("Executed");
        } else {
            executor.execute(() -> {
                ThreadContext.put(AufJmsProperties.MSG_TYPE, msg.type());
                ThreadContext.put(AufJmsProperties.CORRELATION_ID, msg.correlationId());
                LOGGER.atTrace().log("Executing");

                runnable.run();

                LOGGER.atTrace().log("Executed");
                ThreadContext.remove(AufJmsProperties.MSG_TYPE);
                ThreadContext.remove(AufJmsProperties.CORRELATION_ID);
            });
        }
    };

    private static Runnable newRunnable(final JmsMsg msg, final Executable target,
            final ExecutableBinder binder) {
        return () -> {
            final var bindOutcome = CatchingInvocation.invoke(() -> binder.bind(target, () -> msg));
            final var outcome = bindOutcome.ifReturnedPresent().map(ReflectingInvocation::invoke)
                    .orElseGet(() -> InvocationOutcome.thrown(bindOutcome.getThrown()));

            final var postEexcution = target.postExecution();
            if (postEexcution == null) {
                return;
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
        };
    }
}
