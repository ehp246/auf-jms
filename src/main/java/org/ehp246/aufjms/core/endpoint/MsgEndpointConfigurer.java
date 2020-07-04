package org.ehp246.aufjms.core.endpoint;

import java.util.Set;
import java.util.concurrent.Executor;

import javax.jms.MessageListener;

import org.ehp246.aufjms.api.endpoint.InvocationBinder;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.slf4j.MdcKeys;
import org.ehp246.aufjms.core.configuration.AufJmsProperties;
import org.ehp246.aufjms.util.ToMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

/**
 * JmsListenerConfigurer to register runtime-defined Endpoint's.
 *
 * @author Lei Yang
 *
 */
public class MsgEndpointConfigurer implements JmsListenerConfigurer {
	private final static Logger LOGGER = LoggerFactory.getLogger(MsgEndpointConfigurer.class);

	private final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory;
	private final Set<MsgEndpoint> endpoints;
	private final DestinationNameResolver destintationNameResolver;
	private final Executor actionExecutor;
	private final InvocationBinder binder;

	public MsgEndpointConfigurer(
			final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory,
			final Set<MsgEndpoint> endpoints, final DestinationNameResolver destintationNameResolver,
			@Qualifier(AufJmsProperties.EXECUTOR_BEAN) final Executor actionExecutor, final InvocationBinder binder) {
		super();
		this.listenerContainerFactory = listenerContainerFactory;
		this.endpoints = endpoints;
		this.destintationNameResolver = destintationNameResolver;
		this.actionExecutor = actionExecutor;
		this.binder = binder;
	}

	@Override
	public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
		this.endpoints.stream().forEach(endpoint -> {
			LOGGER.debug("Registering endpoint on destination '{}'", endpoint.getDestinationName());

			final var defaultMsgDispatcher = new DefaultMsgDispatcher(endpoint.getResolver(), binder, actionExecutor);
			final var id = endpoint.getDestinationName() + "@" + MsgEndpoint.class.getCanonicalName();

			registrar.registerEndpoint(new JmsListenerEndpoint() {

				@Override
				public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
					final AbstractMessageListenerContainer container = (AbstractMessageListenerContainer) listenerContainer;
					container.setDestinationName(endpoint.getDestinationName());
					container.setDestinationResolver((session, destinationName,
							pubSubDomain) -> destintationNameResolver.resolve(destinationName));
					container.setupMessageListener((MessageListener) message -> {
						final var msg = ToMsg.from(message);

						MDC.put(MdcKeys.MSG_TYPE, msg.getType());
						MDC.put(MdcKeys.CORRELATION_ID, msg.getCorrelationId());

						defaultMsgDispatcher.dispatch(msg);

						MDC.remove(MdcKeys.MSG_TYPE);
						MDC.remove(MdcKeys.CORRELATION_ID);
					});
				}

				@Override
				public String getId() {
					return id;
				}
			}, listenerContainerFactory);
		});
	}
}
