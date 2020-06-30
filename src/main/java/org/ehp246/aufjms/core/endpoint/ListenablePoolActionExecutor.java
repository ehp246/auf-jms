package org.ehp246.aufjms.core.endpoint;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.ActionInvocationBinder;
import org.ehp246.aufjms.api.endpoint.BoundInstance;
import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.api.slf4j.MdcKeys;
import org.ehp246.aufjms.core.reflection.CatchingInvoke;
import org.ehp246.aufjms.core.reflection.InvocationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.task.AsyncListenableTaskExecutor;

/**
 * 
 * @author Lei Yang
 *
 */
public class ListenablePoolActionExecutor implements ActionExecutor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenablePoolActionExecutor.class);

	private final ActionInvocationBinder binder;
	private final AsyncListenableTaskExecutor pool;

	public ListenablePoolActionExecutor(final AsyncListenableTaskExecutor pool, final ActionInvocationBinder binder) {
		super();
		this.pool = Objects.requireNonNull(pool);
		this.binder = Objects.requireNonNull(binder);
	}

	@Override
	public CompletableFuture<ExecutedInstance> submit(final BoundInstance task) {

		if (task.getResolvedInstance().getInvocationModel() == InvocationModel.SYNC) {
			try {
				return CompletableFuture.completedFuture(this.execute(task));
			} catch (Exception e) {
				LOGGER.error("Executiong failed:", e);
				final var future = new CompletableFuture<ExecutedInstance>();
				future.completeExceptionally(e);
				return future;
			}
		}

		final var future = new CompletableFuture<ExecutedInstance>();

		this.pool.submitListenable(() -> {
			MDC.put(MdcKeys.MSG_TYPE, task.getMsg().getType());
			MDC.put(MdcKeys.CORRELATION_ID, task.getMsg().getCorrelationId());

			return this.execute(task);
		}).addCallback(executed -> {
			MDC.put(MdcKeys.MSG_TYPE, task.getMsg().getType());
			MDC.remove(MdcKeys.CORRELATION_ID);

			future.complete(executed);
		}, e -> {
			LOGGER.error("Execution failed:", e);
			MDC.put(MdcKeys.MSG_TYPE, task.getMsg().getType());
			MDC.remove(MdcKeys.CORRELATION_ID);

			future.completeExceptionally(e);
		});

		return future;
	}

	private ExecutedInstance execute(final BoundInstance task) {
		final var msg = task.getMsg();

		LOGGER.trace("Executing");

		final var resolved = task.getResolvedInstance();

		final var bindOutcome = CatchingInvoke.invoke(() -> binder.bind(resolved, () -> msg));

		final var outcome = bindOutcome.getReturned().invoke();

		final var performed = new ExecutedInstance() {

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
		};

		Optional.ofNullable(task.getResolvedInstance().postExecution()).ifPresent(consumer -> {
			LOGGER.trace("Executing postExecution");

			try {
				consumer.accept(performed);
			} catch (Exception e) {
				LOGGER.error("postExecution failed:", e);
			}
		});

		LOGGER.trace("Executed");

		return performed;
	}
}
