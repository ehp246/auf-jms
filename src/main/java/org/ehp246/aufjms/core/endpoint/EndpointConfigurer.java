package org.ehp246.aufjms.core.endpoint;

import java.util.Set;
import java.util.UUID;

import javax.jms.MessageListener;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.util.ToMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EndpointConfigurer implements JmsListenerConfigurer {
	private final static Logger LOGGER = LoggerFactory.getLogger(EndpointConfigurer.class);

	private final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory;
	private final Set<MsgEndpoint> endpoints;
	private final DestinationNameResolver destintationNameResolver;
	private final ActionExecutor actionExecutor;

	public EndpointConfigurer(
			final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory,
			final Set<MsgEndpoint> endpoints, final DestinationNameResolver destintationNameResolver,
			final ActionExecutor actionExecutor) {
		super();
		this.listenerContainerFactory = listenerContainerFactory;
		this.endpoints = endpoints;
		this.destintationNameResolver = destintationNameResolver;
		this.actionExecutor = actionExecutor;
	}

	@Override
	public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
		this.endpoints.stream().forEach(endpoint -> {
			LOGGER.debug("Registering endpoint on destination '{}'", endpoint.getDestinationName());

			final var defaultMsgDispatcher = new DefaultMsgDispatcher(endpoint.getResolver(), actionExecutor);
			final var id = UUID.randomUUID().toString();

			registrar.registerEndpoint(new JmsListenerEndpoint() {

				@Override
				public void setupListenerContainer(MessageListenerContainer listenerContainer) {
					final AbstractMessageListenerContainer container = (AbstractMessageListenerContainer) listenerContainer;
					container.setDestinationName(endpoint.getDestinationName());
					container.setDestinationResolver((session, destinationName,
							pubSubDomain) -> destintationNameResolver.resolve(destinationName));
					container.setupMessageListener((MessageListener) message -> {
						defaultMsgDispatcher.dispatch(ToMsg.from(message));
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
