package org.ehp246.aufjms.core.endpoint;

import java.util.Set;

import javax.jms.MessageListener;

import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

/**
 * JmsListenerConfigurer to register runtime-defined Action endpoint's.
 * 
 * @author Lei Yang
 *
 */
public class EndpointConfigurer implements JmsListenerConfigurer {
	private final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory;
	private final Set<MsgEndpoint> endpoints;
	private final DestinationNameResolver destintationNameResolver;

	public EndpointConfigurer(
			final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory,
			final Set<MsgEndpoint> endpoints, final DestinationNameResolver destintationNameResolver) {
		super();
		this.listenerContainerFactory = listenerContainerFactory;
		this.endpoints = endpoints;
		this.destintationNameResolver = destintationNameResolver;
	}

	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		this.endpoints.stream().forEach(endpoint -> registrar.registerEndpoint(new JmsListenerEndpoint() {

			@Override
			public void setupListenerContainer(MessageListenerContainer listenerContainer) {
				final AbstractMessageListenerContainer abstractMessageListenerContainer = (AbstractMessageListenerContainer) listenerContainer;
				abstractMessageListenerContainer
						.setDestination(destintationNameResolver.resolve(endpoint.getDestinationName()));
				abstractMessageListenerContainer.setupMessageListener((MessageListener) endpoint::onMessage);
			}

			@Override
			public String getId() {
				return endpoint.getId();
			}
		}, listenerContainerFactory));
	}
}
