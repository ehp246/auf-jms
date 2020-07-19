package org.ehp246.aufjms.core.endpoint;

import java.util.concurrent.Executor;

import org.ehp246.aufjms.api.endpoint.ExecutableBinder;
import org.ehp246.aufjms.api.endpoint.ExecutableResolver;
import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.InvocationMode;
import org.ehp246.aufjms.api.endpoint.MsgDispatcher;
import org.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.api.slf4j.MdcKeys;
import org.ehp246.aufjms.core.configuration.AufJmsProperties;
import org.ehp246.aufjms.core.reflection.CatchingInvocation;
import org.ehp246.aufjms.core.reflection.InvocationOutcome;
import org.ehp246.aufjms.core.reflection.ReflectingInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Lei Yang
 *
 */
public class DefaultMsgDispatcher implements MsgDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMsgDispatcher.class);

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
	public void dispatch(final Msg msg) {
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

		if (resolved.getInvocationMode() == null || resolved.getInvocationMode() == InvocationMode.SYNC) {
			LOGGER.trace("Executing");

			runnable.run();

			LOGGER.trace("Executed");
		} else {
			executor.execute(() -> {
				MDC.put(MdcKeys.MSG_TYPE, msg.getType());
				MDC.put(MdcKeys.CORRELATION_ID, msg.getCorrelationId());
				LOGGER.trace("Executing");

				runnable.run();

				LOGGER.trace("Executed");
				MDC.remove(MdcKeys.MSG_TYPE);
				MDC.remove(MdcKeys.CORRELATION_ID);
			});
		}
	};

	private static Runnable newRunnable(final Msg msg, final ResolvedExecutable resolved,
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
					public Msg getMsg() {
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
