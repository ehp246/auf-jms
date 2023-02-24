package me.ehp246.aufjms.core.inbound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;

import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocableBinder;
import me.ehp246.aufjms.api.inbound.InvocableDispatcher;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.InvocationModel;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class DefaultInvocableDispatcher implements InvocableDispatcher {
    private final static Logger LOGGER = LogManager.getLogger(InvocableDispatcher.class);

    private final Executor executor;
    private final InvocableBinder binder;
    private final List<InvocationListener.OnCompleted> completed = new ArrayList<>();
    private final List<InvocationListener.OnFailed> failed = new ArrayList<>();

    DefaultInvocableDispatcher(final InvocableBinder binder, @Nullable final List<InvocationListener> listeners,
            @Nullable final Executor executor) {
        super();
        this.binder = binder;
        this.executor = executor;
        for (final var listener : listeners == null ? List.of() : listeners) {
            // null tolerating
            if (listener instanceof final InvocationListener.OnCompleted completed) {
                this.completed.add(completed);
            }
            if (listener instanceof final InvocationListener.OnFailed failed) {
                this.failed.add(failed);
            }
        }
    }

    @Override
    public void dispatch(final Invocable invocable, final JmsMsg msg) {
        /*
         * The runnable returned is expected to handle all execution and exception. The
         * caller simply invokes this runnable without further processing.
         */
        final var runnable = (Runnable) () -> {
            try {
                final var bound = binder.bind(invocable, msg);

                assert (bound != null);

                final var outcome = bound.invoke();

                assert (outcome != null);

                if (outcome instanceof final Failed failed) {
                    if (DefaultInvocableDispatcher.this.failed.size() == 0) {
                        throw failed.thrown();
                    }

                    try {
                        for (final var listener : DefaultInvocableDispatcher.this.failed) {
                            listener.onFailed(failed);
                        }

                        /*
                         * If none throws any exception, skip further execution and acknowledge the
                         * message.
                         */
                        return;
                    } catch (final Throwable e) {
                        LOGGER.atTrace().log("Failure interceptor threw: {}", e::getMessage);

                        throw e;
                    }
                }

                assert (outcome instanceof Completed);

                final var completed = (Completed) outcome;

                try {
                    DefaultInvocableDispatcher.this.completed.forEach(listener -> listener.onCompleted(completed));
                } catch (final Exception e) {
                    LOGGER.atTrace().log("Completed listener failed: {}", e::getMessage);

                    throw e;
                }
            } catch (final Throwable e) {
                throw OneUtil.ensureRuntime(e);
            } finally {
                try (invocable) {
                } catch (final Exception e) {
                    LOGGER.atError().withThrowable(e).log("Close failed, ignored: {}", e::getMessage);
                }
            }
        };

        if (executor == null || invocable.invocationModel() == InvocationModel.INLINE) {

            runnable.run();

        } else {
            executor.execute(() -> {
                try {
                    Log4jContext.set(msg);

                    runnable.run();

                } finally {
                    Log4jContext.clearMsg();
                }
            });
        }
    };
}
