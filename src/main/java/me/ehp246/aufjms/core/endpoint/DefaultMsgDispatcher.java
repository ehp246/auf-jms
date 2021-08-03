package me.ehp246.aufjms.core.endpoint;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Qualifier;

import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.MsgDispatcher;
import me.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.slf4j.MdcKeys;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.reflection.CatchingInvocation;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;
import me.ehp246.aufjms.core.reflection.ReflectingInvocation;

/**
 *
 * @author Lei Yang
 *
 */
public class DefaultMsgDispatcher implements MsgDispatcher {
    private static final Logger LOGGER = LogManager.getLogger(DefaultMsgDispatcher.class);

    private final ExecutableResolver actionResolver;
    private final Executor executor;
    private final ExecutableBinder binder;

    public DefaultMsgDispatcher(final ExecutableResolver actionResolver, final ExecutableBinder binder,
            @Qualifier(AufJmsProperties.EXECUTOR_BEAN) final Executor executor) {
        super();
        this.actionResolver = actionResolver;
        this.binder = binder;
        this.executor = executor;
    }

    @Override
    public void dispatch(final JmsMsg msg) {
        LOGGER.trace("Dispatching");

        final var resolveOutcome = CatchingInvocation.invoke(() -> this.actionResolver.resolve(msg));
        if (resolveOutcome.hasThrown()) {
            LOGGER.error("Resolution failed", resolveOutcome.getThrown());
            return;
        }

        final var resolved = resolveOutcome.getReturned();
        if (resolved == null) {
            LOGGER.info("Un-matched message");
            return;
        }

        LOGGER.trace("Submitting");

        final var runnable = newRunnable(msg, resolved, binder);

        if (resolved.getInvocationModel() == null || resolved.getInvocationModel() == InvocationModel.SYNC) {
            LOGGER.trace("Executing");

            runnable.run();

            LOGGER.trace("Executed");
        } else {
            executor.execute(() -> {
                ThreadContext.put(MdcKeys.MSG_TYPE, msg.type());
                ThreadContext.put(MdcKeys.CORRELATION_ID, msg.correlationId());
                LOGGER.trace("Executing");

                runnable.run();

                LOGGER.trace("Executed");
                ThreadContext.remove(MdcKeys.MSG_TYPE);
                ThreadContext.remove(MdcKeys.CORRELATION_ID);
            });
        }
    };

    private static Runnable newRunnable(final JmsMsg msg, final ResolvedExecutable resolved,
            final ExecutableBinder binder) {
        return () -> {
            final var bindOutcome = CatchingInvocation.invoke(() -> binder.bind(resolved, () -> msg));
            final var outcome = bindOutcome.ifReturnedPresent().map(ReflectingInvocation::invoke)
                    .orElseGet(() -> InvocationOutcome.thrown(bindOutcome.getThrown()));

            final var postEexcution = resolved.postExecution();
            if (postEexcution == null) {
                return;
            }

            try {
                LOGGER.trace("Executing postExecution");

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
                    public ResolvedExecutable getInstance() {
                        return resolved;
                    }
                });

                LOGGER.trace("Executed postExecution");
            } catch (final Exception e) {
                LOGGER.error("postExecution failed:", e);
            }
        };
    }
}
