package org.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.ActionInvocationBinder;
import org.ehp246.aufjms.api.endpoint.ActionInvocationContext;
import org.ehp246.aufjms.api.endpoint.ExecutableInstance;
import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.ExecutionModel;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.Msg;
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
	private static final String TRACE_ID = "Msg-Correlation-Id";

	private final Set<ExecutableInstance> executionTask = ConcurrentHashMap.newKeySet();

	private final ActionInvocationBinder binder;
	private final AsyncListenableTaskExecutor pool;

	public ListenablePoolActionExecutor(final AsyncListenableTaskExecutor pool, final ActionInvocationBinder binder) {
		super();
		this.pool = Objects.requireNonNull(pool);
		this.binder = Objects.requireNonNull(binder);
	}

	@Override
	public CompletableFuture<ExecutedInstance> submit(final ExecutableInstance task) {

		if (task.getResolvedInstance().getExecutionModel() == ExecutionModel.SYNC) {
			try {
				return CompletableFuture.completedFuture(this.execute(task));
			} catch (Exception e) {
				LOGGER.error("", e);
				final var future = new CompletableFuture<ExecutedInstance>();
				future.completeExceptionally(e);
				return future;
			}
		}

		executionTask.add(task);

		final var future = new CompletableFuture<ExecutedInstance>();

		this.pool.submitListenable(() -> {
			MDC.put(TRACE_ID, task.getMsg().getCorrelationId());
			return this.execute(task);
		}).addCallback(executed -> {
			MDC.remove(TRACE_ID);
			executionTask.remove(task);
			future.complete(executed);
		}, e -> {
			LOGGER.error("", e);
			MDC.remove(TRACE_ID);
			executionTask.remove(task);
			future.completeExceptionally(e);
		});

		return future;
	}

	private ExecutedInstance execute(final ExecutableInstance task) {
		final var msg = task.getMsg();
		final var resolved = task.getResolvedInstance();

		final var bindOutcome = CatchingInvoke.invoke(() -> binder.bind(resolved, new ActionInvocationContext() {
			@Override
			public Msg getMsg() {
				return msg;
			}
		}));

		final var outcome = bindOutcome.getReturned().invoke();

		final var performed = new ExecutedInstance() {

			@Override
			public InvocationOutcome<?> getOutcome() {
				return outcome;
			}

			@Override
			public Msg getMq() {
				return msg;
			}

			@Override
			public ResolvedInstance getInstance() {
				return resolved;
			}
		};

		Optional.ofNullable(task.postPerforms()).map(List::stream).orElseGet(Stream::empty).forEach(consumer -> {
			consumer.accept(performed);
		});

		return performed;
	}
}
