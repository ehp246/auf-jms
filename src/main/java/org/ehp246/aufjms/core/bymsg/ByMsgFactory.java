package org.ehp246.aufjms.core.bymsg;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Objects;

import javax.jms.Destination;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.MessagePortDestinationSupplier;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.ehp246.aufjms.core.reflection.ProxyInvoked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class ByMsgFactory {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByMsgFactory.class);

	private final ReplyEndpointConfiguration replyConfig;
	private final MessagePortProvider portProvider;
	private final DestinationNameResolver nameResolver;

	public ByMsgFactory(final MessagePortProvider portProvider, final DestinationNameResolver nameResolver,
			final ReplyEndpointConfiguration replyConfig) {
		super();
		this.portProvider = Objects.requireNonNull(portProvider);
		this.nameResolver = Objects.requireNonNull(nameResolver);
		this.replyConfig = replyConfig;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(final Class<T> annotatedInterface) {
		final var destinatinName = annotatedInterface.getAnnotation(ByMsg.class).value();
		final var port = portProvider.get(new MessagePortDestinationSupplier() {
			private final String replyTo = replyConfig.getReplyToName();

			@Override
			public Destination getTo() {
				return nameResolver.resolve(destinatinName);
			}

			@Override
			public Destination getReplyTo() {
				return replyTo == null ? null : nameResolver.resolve(replyTo);
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

					final var invocation = new ProxyInvocation(new ProxyInvoked<Object>(proxy, method, args),
							replyConfig.getFromBody(), replyConfig.getTimeout());

					if (invocation.isReplyExpected()) {
						replyConfig.getCorrelMap().put(invocation.getCorrelationId(), invocation);
					}

					try {
						port.accept(invocation);
					} catch (Exception e) {
						replyConfig.getCorrelMap().remove(invocation.getCorrelationId());
						throw e;
					}

					return invocation.returnInvocation();
				});

	}
}
