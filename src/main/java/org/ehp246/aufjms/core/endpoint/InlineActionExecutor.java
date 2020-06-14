package org.ehp246.aufjms.core.endpoint;

import java.util.concurrent.CompletableFuture;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.ActionInvocationBinder;
import org.ehp246.aufjms.api.endpoint.ActionInvocationContext;
import org.ehp246.aufjms.api.endpoint.BoundInstance;
import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.reflection.CatchingInvoke;
import org.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * 
 * @author Lei Yang
 *
 */
public class InlineActionExecutor implements ActionExecutor {
	private final ActionInvocationBinder binder;

	public InlineActionExecutor(final ActionInvocationBinder binder) {
		super();
		this.binder = binder;
	}

	@Override
	public CompletableFuture<ExecutedInstance> submit(final BoundInstance task) {
		final var resolved = task.getResolvedInstance();
		final var msg = task.getMsg();

		final var bindOutcome = CatchingInvoke.invoke(() -> binder.bind(resolved, new ActionInvocationContext() {

			@Override
			public Msg getMsg() {
				return msg;
			}
		}));

		// Treat binding exception separately from invocation exception.
		if (bindOutcome.hasThrown()) {
			return CompletableFuture.completedFuture(new ExecutedInstance() {
				private final InvocationOutcome<?> caught = InvocationOutcome.thrown(bindOutcome.getThrown());

				@Override
				public InvocationOutcome<?> getOutcome() {
					return caught;
				}

				@Override
				public Msg getMsg() {
					return msg;
				}

				@Override
				public ResolvedInstance getInstance() {
					return resolved;
				}
			});
		}

		final var outcome = bindOutcome.getReturned().invoke();

		return CompletableFuture.completedFuture(new ExecutedInstance() {

			@Override
			public Msg getMsg() {
				return task.getMsg();
			}

			@Override
			public ResolvedInstance getInstance() {
				return resolved;
			}

			@Override
			public InvocationOutcome<?> getOutcome() {
				return outcome;
			}

		});
	}

}
