package org.ehp246.aufjms.core.bymsg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.OfCorrelationId;
import org.ehp246.aufjms.annotation.OfGroup;
import org.ehp246.aufjms.annotation.OfType;
import org.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import org.ehp246.aufjms.api.exception.ExecutionThrown;
import org.ehp246.aufjms.api.exception.ForMsgExecutionException;
import org.ehp246.aufjms.api.jms.FromBody;
import org.ehp246.aufjms.api.jms.MessageSupplier;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.reflection.ProxyInvoked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lei Yang
 */
class ByMsgInvocation implements MessageSupplier, ResolvedExecutable {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByMsgInvocation.class);

	private static final Method ONREPLY;

	static {
		try {
			ONREPLY = ByMsgInvocation.class.getDeclaredMethod("onReply", Msg.class);
		} catch (final Exception e) {
			LOGGER.error("This should not happen. Did you change the method signature?");
			throw new RuntimeException(e);
		}
	}

	private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfCorrelationId.class,
			OfType.class, OfGroup.class);

	private final CompletableFuture<Object> future = new CompletableFuture<>();
	private final ProxyInvoked<Object> invoked;
	private final String correlationId;
	private final FromBody<String> fromBody;
	private final long timeout;
	private final long ttl;

	public ByMsgInvocation(final ProxyInvoked<Object> invoked, final FromBody<String> fromBody, final Long timeout,
			final Long ttl) {
		super();

		this.invoked = invoked;
		final var found = this.invoked.findOnArguments(OfCorrelationId.class);
		this.correlationId = found.isPresent()
				? Optional.ofNullable(found.get().getArgument()).map(Object::toString).orElse(null)
				: UUID.randomUUID().toString();
		this.fromBody = fromBody;
		this.timeout = timeout;
		this.ttl = ttl;
	}

	public boolean isReplyExpected() {
		return this.invoked.getReturnType() != void.class;
	}

	public Object returnInvocation() throws Throwable {
		if (!this.isReplyExpected()) {
			this.future.complete(null);
			return null;
		}

		// Wait with timeout.
		try {
			if (timeout <= 0) {
				// No timeout
				return this.future.get();
			}

			return this.future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (final Exception e) {
			final var rethrow = e instanceof ExecutionException ? e.getCause() : e;
			if (invoked.getThrows().stream().filter(declared -> declared.isAssignableFrom(rethrow.getClass())).findAny()
					.isPresent() || rethrow instanceof RuntimeException) {
				throw rethrow;
			} else {
				throw new RuntimeException(rethrow);
			}
		}
	}

	public void onReply(final Msg msg) {
		LOGGER.trace("Received reply");

		try {
			if (msg.isException()) {
				this.fromBody.from(msg.getBodyAsText(), new FromBody.Receiver<ExecutionThrown>() {

					@Override
					public Class<ExecutionThrown> getType() {
						return ExecutionThrown.class;
					}

					@Override
					public void receive(final ExecutionThrown value) {
						future.completeExceptionally(new ForMsgExecutionException(value.getCode(), value.getMessage()));
					}
				});
				return;
			}

			this.fromBody.from(msg.getBodyAsText(), new FromBody.Receiver<>() {

				@Override
				public List<? extends Annotation> getAnnotations() {
					return List.of(invoked.getMethod().getAnnotations());
				}

				@Override
				public Class<?> getType() {
					return invoked.getReturnType();
				}

				@Override
				public void receive(final Object value) {
					future.complete(value);
				}

			});
		} catch (final Exception e) {
			LOGGER.error("onReply failed: {}", e.getMessage(), e);

			future.completeExceptionally(e);
		}
	}

	@Override
	public String getType() {
		final var onArgs = invoked.findOnArguments(OfType.class);
		if (onArgs.isPresent()) {
			final var value = onArgs.get().getAnnotation().value();
			if (!value.isEmpty()) {
				// Annotated value takes precedence.
				return value;
			}
			// If there is no annotated value, return argument value even if null.
			return Optional.ofNullable(onArgs.get().getArgument()).map(Object::toString).orElse(null);
		}

		final var onMethod = invoked.findOnMethod(OfType.class);

		if (onMethod.isPresent()) {
			final var type = onMethod.get().value();
			if (type.isBlank()) {
				// Method has the annotation without a specified value, use method name.
				return invoked.getMethodNameCapped();
			} else {
				return type;
			}
		}

		return invoked.findOnDeclaringClass(OfType.class).map(OfType::value)
				.orElseGet(invoked::getSimpleDeclaringClassName);
	}

	@Override
	public Long getTtl() {
		return ttl;
	}

	@Override
	public String getInvoking() {
		return invoked.findOnMethod(Invoking.class).map(Invoking::value).filter(Predicate.not(String::isBlank))
				.orElseGet(invoked::getMethodName);
	}

	@Override
	public String getCorrelationId() {
		return this.correlationId;
	}

	@Override
	public List<?> getBodyValues() {
		return invoked.filterValueArgs(PARAMETER_ANNOTATIONS);
	}

	@Override
	public Object getInstance() {
		return this;
	}

	@Override
	public Method getMethod() {
		return ONREPLY;
	}
}
