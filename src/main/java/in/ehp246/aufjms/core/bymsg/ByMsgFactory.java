package in.ehp246.aufjms.core.bymsg;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Objects;

import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.ehp246.aufjms.api.annotation.ByMsg;
import in.ehp246.aufjms.api.jms.DestinationNameResolver;
import in.ehp246.aufjms.api.jms.MsgPortDestinationSupplier;
import in.ehp246.aufjms.api.jms.MsgPortProvider;
import in.ehp246.aufjms.core.reflection.ProxyInvoked;
import in.ehp246.aufjms.core.reflection.ReflectingType;

/**
 *
 * @author Lei Yang
 *
 */
public class ByMsgFactory {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByMsgFactory.class);

	private final ReplyEndpointConfiguration replyConfig;
	private final MsgPortProvider portProvider;
	private final DestinationNameResolver nameResolver;

	public ByMsgFactory(final MsgPortProvider portProvider, final DestinationNameResolver nameResolver,
			final ReplyEndpointConfiguration replyConfig) {
		super();
		this.portProvider = Objects.requireNonNull(portProvider);
		this.nameResolver = Objects.requireNonNull(nameResolver);
		this.replyConfig = replyConfig;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(final Class<T> annotatedInterface) {
		final var destinatinName = annotatedInterface.getAnnotation(ByMsg.class).value();
		final var port = portProvider.get(new MsgPortDestinationSupplier() {
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

		final var reflectedInterface = new ReflectingType<>(annotatedInterface);
		final var timeout = reflectedInterface.findOnType(ByMsg.class).map(ByMsg::timeout).filter(i -> i > 0)
				.orElseGet(replyConfig::getTimeout);
		final var ttl = reflectedInterface.findOnType(ByMsg.class).map(ByMsg::ttl).filter(i -> i > 0)
				.orElseGet(replyConfig::getTtl);

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

					final var invocation = new ByMsgInvocation(new ProxyInvoked<Object>(proxy, method, args),
							replyConfig.getFromBody(), timeout, ttl);
					final var correlMap = replyConfig.getCorrelMap();

					if (invocation.isReplyExpected()) {
						correlMap.put(invocation.getCorrelationId(), invocation);
					}

					try {
						port.accept(invocation);
					} catch (final Exception e) {
						correlMap.remove(invocation.getCorrelationId());
						throw e;
					}

					try {
						return invocation.returnInvocation();
					} catch (final Exception e) {
						throw e;
					} finally {
						correlMap.remove(invocation.getCorrelationId());
					}
				});

	}
}
