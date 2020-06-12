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

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.OfCorrelationId;
import org.ehp246.aufjms.annotation.OfGroup;
import org.ehp246.aufjms.annotation.OfTimeout;
import org.ehp246.aufjms.annotation.OfType;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.api.jms.MsgSupplier;
import org.ehp246.aufjms.core.reflection.ProxyInvoked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 */
class ProxyInvocation implements MsgSupplier, ResolvedInstance {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProxyInvocation.class);

	private static final Method ONREPLY;

	static {
		try {
			ONREPLY = ProxyInvocation.class.getDeclaredMethod("onReply", Msg.class);
		} catch (Exception e) {
			LOGGER.error("This should not happen. Did you change the method signature?");
			throw new RuntimeException(e);
		}
	}

	private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfCorrelationId.class,
			OfType.class, OfGroup.class);

	private final CompletableFuture<Object> future = new CompletableFuture<>();
	private final ProxyInvoked<Object> invoked;
	private final String correlationId;
	private final long timeout;

	public ProxyInvocation(Object target, Method method, Object[] args) {
		super();

		this.invoked = new ProxyInvoked<Object>(target, method, args);

		final var found = this.invoked.findUpArgument(OfCorrelationId.class);

		this.correlationId = found.isPresent()
				? Optional.ofNullable(found.get().getArgument()).map(Object::toString).orElse(null)
				: UUID.randomUUID().toString();
		this.timeout = this.invoked.findOnMethod(OfTimeout.class).map(OfTimeout::value).orElse((long) -1);
	}

	public boolean isReplyExpected() {
		final Class<?> returnType = this.invoked.getReturnType();
		return returnType != void.class && returnType != Void.class;
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
		} catch (Exception e) {
			final Throwable rethrow = e instanceof ExecutionException ? e.getCause() : e;
			if (invoked.getThrows().stream().filter(declared -> declared.isAssignableFrom(rethrow.getClass())).findAny()
					.isPresent()) {
				throw rethrow;
			} else {
				throw new RuntimeException(rethrow);
			}
		}
	}

	public void onReply(Msg msg) {
	}

	@Override
	public String getType() {
		final var found = invoked.findUpArgument(OfType.class);
		if (found.isPresent()) {
			final var value = found.get().getAnnotation().value();
			if (!value.isEmpty()) {
				// Annotated value takes precedence.
				return value;
			}
			// If there is no annotated value, return argument value even if null.
			return Optional.ofNullable(found.get().getArgument()).map(Object::toString).orElse(null);
		}

		final var type = invoked.valueOnMethod(OfType.class, OfType::value, () -> "");

		if (!type.isEmpty()) {
			return type;
		}

		return invoked.getMethodNameCapped();
	}

	@Override
	public String getCorrelationId() {
		return this.correlationId;
	}

	@Override
	public String getTo() {
		return invoked.findOnDeclaringClass(ByMsg.class).map(ByMsg::value).orElse("");
	}

	@Override
	public List<?> getBodyValue() {
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