package org.ehp246.aufjms.core.bymsg;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 
 * @author Lei Yang
 *
 */
public class ProxyFactory {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProxyFactory.class);

	private final MessagePortProvider portProvider;
	private final DestinationNameResolver nameResolver;
	private final Map<String, ResolvedInstance> correlMap;

	public ProxyFactory(
			final @Qualifier(ReplyToConfiguration.BEAN_NAME_CORRELATION_MAP) Map<String, ResolvedInstance> correlMap,
			final MessagePortProvider pipeSupplier, final DestinationNameResolver nameResolver) {
		super();
		this.portProvider = Objects.requireNonNull(pipeSupplier);
		this.correlMap = Objects.requireNonNull(correlMap);
		this.nameResolver = Objects.requireNonNull(nameResolver);
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(final Class<T> annotatedInterface) {
		final var destinatinName = annotatedInterface.getAnnotation(ByMsg.class).value();
		final var port = portProvider.get(() -> nameResolver.resolve(destinatinName));

		LOGGER.debug("Proxying {} to {}", annotatedInterface.getCanonicalName(), destinatinName);

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

					final var invocation = new ProxyInvocation(proxy, method, args);

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
