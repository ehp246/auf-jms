package me.ehp246.aufjms.core.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;

import me.ehp246.aufjms.api.endpoint.BoundInvoker;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class InvocableDispatcher {
    private static final Logger LOGGER = LogManager.getLogger(InvocableDispatcher.class);

    private final Executor executor;
    private final InvocableBinder binder;
    private final BoundInvoker invoker;
    private final List<InvocationListener.OnCompleted> completed = new ArrayList<>();
    private final List<InvocationListener.OnFailed> failed = new ArrayList<>();

    InvocableDispatcher(final InvocableBinder binder, final BoundInvoker invoker,
            @Nullable final List<InvocationListener> listeners, 
            @Nullable final Executor executor) {
        super();
        this.binder = binder;
        this.executor = executor;
        this.invoker = invoker;
        for (final var listener : listeners == null ? List.of() : listeners) {
            // null tolerating
            if (listener instanceof InvocationListener.OnCompleted completed) {
                this.completed.add(completed);
            }
            if (listener instanceof InvocationListener.OnFailed failed) {
                this.failed.add(failed);
            }
        }
    }

    public void dispatch(final Invocable invocable, final MsgContext msgCtx) {
        final var runnable = newRunnable(invocable, msgCtx);

        if (executor == null || invocable.invocationModel() == InvocationModel.INLINE) {

            runnable.run();

        } else {
            executor.execute(() -> {
                try {
                    AufJmsContext.set(msgCtx.session());
                    Log4jContext.set(msgCtx.msg());

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
     * @param target
     * @param msg
     * 
     * @return
     */
    private Runnable newRunnable(final Invocable target, final MsgContext msgCtx) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    final var bound = binder.bind(target, msgCtx);

                    assert (bound != null);

                    final var outcome = invoker.apply(bound);

                    assert (outcome != null);

                    if (outcome instanceof Failed failed) {
                        if (InvocableDispatcher.this.failed.size() == 0) {
                            throw failed.thrown();
                        }

                        try {
                            LOGGER.atTrace().log("Executing failed interceptor");

                            for (final var listener : InvocableDispatcher.this.failed) {
                                listener.onFailed(failed);
                            }

                            LOGGER.atTrace().log("Failure interceptor invoked");
                            /*
                             * If none throws, skip further execution and acknowledge the message.
                             */
                            return;
                        } catch (Exception e) {
                            LOGGER.atTrace().withThrowable(e).log("Failure interceptor threw: {}", e::getMessage);

                            throw e;
                        }
                    }

                    assert (outcome instanceof Completed);

                    final var completed = (Completed) outcome;

                    try {
                        InvocableDispatcher.this.completed.forEach(listener -> listener.onCompleted(completed));

                        LOGGER.atTrace().log("Completed listener invoked");
                    } catch (Exception e) {
                        LOGGER.atTrace().withThrowable(e).log("Completed listener failed: {}", e.getMessage());

                        throw e;
                    }
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
}