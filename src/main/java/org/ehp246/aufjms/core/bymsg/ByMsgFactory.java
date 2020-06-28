package org.ehp246.aufjms.core.bymsg;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;

import javax.jms.Destination;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.FromBody;
import org.ehp246.aufjms.api.jms.MessagePortDestinationSupplier;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.ehp246.aufjms.api.jms.ReplyToNameSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 
 * @author Lei Yang
 *
 */
public class ByMsgFactory {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByMsgFactory.class);

	private final MessagePortProvider portProvider;
	private final DestinationNameResolver nameResolver;
	private final Map<String, ResolvedInstance> correlMap;
	private final ReplyToNameSupplier replyToSupplier;
	private final FromBody<String> fromBody;

	public ByMsgFactory(
			final @Qualifier(ReplyConfiguration.BEAN_NAME_CORRELATION_MAP) Map<String, ResolvedInstance> correlMap,
			final MessagePortProvider pipeSupplier, final DestinationNameResolver nameResolver,
			final ReplyToNameSupplier replyToSupplier, final FromBody<String> fromBody) {
		super();
		this.portProvider = Objects.requireNonNull(pipeSupplier);
		this.correlMap = Objects.requireNonNull(correlMap);
		this.nameResolver = Objects.requireNonNull(nameResolver);
		// Allow null to forego request/response support.
		this.replyToSupplier = replyToSupplier;
		this.fromBody = fromBody;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(final Class<T> annotatedInterface) {
		final var destinatinName = annotatedInterface.getAnnotation(ByMsg.class).value();
		final var port = portProvider.get(new MessagePortDestinationSupplier() {

			@Override
			public Destination getTo() {
				return nameResolver.resolve(destinatinName);
			}

			@Override
			public Destination getReplyTo() {
				return replyToSupplier == null ? null : nameResolver.resolve(replyToSupplier.get());
			}

		});

		LOGGER.debug("Proxying {}@{}", destinatinName, annotatedInterface.getCanonicalName());

		return (T) Proxy.newProxyInstance(annotatedInterface.getClassLoader(), new Class[] { annotatedInterface },
				(InvocationHandler) (proxy, method, args) -> {
					if (method.getName().equals("toString")) {
						return this.toString();
					}
					if (method.getName().equals("hashCode")) {
						return this.hashCode();
					}
					if (method.getName().equals("equals")) {
						return this.equals(args[0]);
					}
					if (method.isDefault()) {
						return MethodHandles.privateLookupIn(annotatedInterface, MethodHandles.lookup())
								.findSpecial(annotatedInterface, method.getName(),
										MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
										annotatedInterface)
								.bindTo(proxy).invokeWithArguments(args);
					}

					final var invocation = new ProxyInvocation(proxy, method, args, fromBody);

					if (invocation.isReplyExpected()) {
						this.correlMap.put(invocation.getCorrelationId(), invocation);
					}

					try {
						port.accept(invocation);
					} catch (Exception e) {
						this.correlMap.remove(invocation.getCorrelationId());
						throw e;
					}

					return invocation.returnInvocation();
				});

	}
}