package me.ehp246.aufjms.core.inbound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.Nullable;

import me.ehp246.aufjms.api.exception.BoundInvocationFailedException;
import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocableBinder;
import me.ehp246.aufjms.api.inbound.InvocableDispatcher;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.InvocationModel;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;

/**
 * @author Lei Yang
 *
 */
final class DefaultInvocableDispatcher implements InvocableDispatcher {
    private final static Logger LOGGER = LogManager.getLogger(InvocableDispatcher.class);

    private final Executor executor;
    private final InvocableBinder binder;
    private final List<InvocationListener.OnInvoking> invoking = new ArrayList<>();
    private final List<InvocationListener.OnCompleted> completed = new ArrayList<>();
    private final List<InvocationListener.OnFailed> failed = new ArrayList<>();

    public DefaultInvocableDispatcher(final InvocableBinder binder, @Nullable final List<InvocationListener> listeners,
            @Nullable final Executor executor) {
        super();
        this.binder = binder;
        this.executor = executor;
        for (final var listener : listeners == null ? List.of() : listeners) {
            // null tolerating
            if (listener instanceof final InvocationListener.OnInvoking invoking) {
                this.invoking.add(invoking);
            }
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
        final var boundRef = new AtomicReference<BoundInvocable>();
        final var runnable = (Runnable) () -> {
            try {
                boundRef.set(binder.bind(invocable, msg));

                final var bound = boundRef.get();

                ThreadContext.putAll(bound.log4jContext() == null ? Map.of() : bound.log4jContext());

                DefaultInvocableDispatcher.this.invoking.forEach(listener -> listener.onInvoking(bound));

                final var outcome = bound.invoke();

                if (outcome instanceof final Failed failed) {
                    for (final var listener : DefaultInvocableDispatcher.this.failed) {
                        try {
                            listener.onFailed(failed);
                        } catch (final Exception e) {
                            failed.thrown().addSuppressed(e);
                        }
                    }

                    if (failed.thrown() instanceof RuntimeException re) {
                        throw re;
                    } else {
                        throw new BoundInvocationFailedException(failed.thrown());
                    }
                }

                final var completed = (Completed) outcome;
                DefaultInvocableDispatcher.this.completed.forEach(listener -> listener.onCompleted(completed));
            } finally {
                try (invocable) {
                } catch (final Exception e) {
                    LOGGER.atWarn().withThrowable(e).withMarker(AufJmsConstants.EXCEPTION).log("Ignored: {}",
                            e::getMessage);
                }
                Optional.ofNullable(boundRef.get()).map(BoundInvocable::log4jContext).map(Map::keySet)
                        .ifPresent(ThreadContext::removeAll);
            }
        };

        if (executor == null || invocable.invocationModel() == InvocationModel.INLINE) {

            runnable.run();

        } else {
            executor.execute(() -> {
                try (final var closeable = Log4jContext.set(msg)) {
                    runnable.run();
                } catch (final Exception e) {
                    LOGGER.atWarn().withThrowable(e).withMarker(AufJmsConstants.EXCEPTION).log("Ignored: {}",
                            e::getMessage);
                }
            });
        }
    };
}
