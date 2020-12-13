package me.ehp246.aufjms.core.endpoint;

import java.util.Set;
import java.util.concurrent.Executor;

import javax.jms.MessageListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.MsgEndpoint;
import me.ehp246.aufjms.api.jms.DestinationNameResolver;
import me.ehp246.aufjms.api.slf4j.MdcKeys;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.util.ToMsg;

/**
 * JmsListenerConfigurer to register runtime-defined Endpoint's.
 *
 * @author Lei Yang
 *
 */
public class MsgEndpointConfigurer implements JmsListenerConfigurer {
	private final static Logger LOGGER = LogManager.getLogger(MsgEndpointConfigurer.class);

	private final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory;
	private final Set<MsgEndpoint> endpoints;
	private final DestinationNameResolver destintationNameResolver;
	private final Executor actionExecutor;
	private final ExecutableBinder binder;

	public MsgEndpointConfigurer(
			final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory,
			final Set<MsgEndpoint> endpoints, final DestinationNameResolver destintationNameResolver,
			@Qualifier(AufJmsProperties.EXECUTOR_BEAN) final Executor actionExecutor, final ExecutableBinder binder) {
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

						ThreadContext.put(MdcKeys.MSG_TYPE, msg.getType());
						ThreadContext.put(MdcKeys.CORRELATION_ID, msg.getCorrelationId());

						defaultMsgDispatcher.dispatch(msg);

						ThreadContext.remove(MdcKeys.MSG_TYPE);
						ThreadContext.remove(MdcKeys.CORRELATION_ID);
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
