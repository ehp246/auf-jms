package me.ehp246.aufjms.core.endpoint;

import java.util.Set;
import java.util.concurrent.Executor;

import javax.jms.MessageListener;
import javax.jms.TextMessage;

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

import me.ehp246.aufjms.api.endpoint.AtEndpoint;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.jms.DestinationNameResolver;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * JmsListenerConfigurer used to register {@link AtEndpoint}'s at run-time.
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class AtEndpointConfigurer implements JmsListenerConfigurer {
    private final static Logger LOGGER = LogManager.getLogger(AtEndpointConfigurer.class);

    private final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory;
    private final Set<AtEndpoint> endpoints;
    private final DestinationNameResolver destintationNameResolver;
    private final Executor executor;
    private final ExecutableBinder binder;

    public AtEndpointConfigurer(
            final JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerContainerFactory,
            final Set<AtEndpoint> endpoints, final DestinationNameResolver destintationNameResolver,
            @Qualifier(AufJmsProperties.EXECUTOR_BEAN) final Executor actionExecutor, final ExecutableBinder binder) {
        super();
        this.listenerContainerFactory = listenerContainerFactory;
        this.endpoints = endpoints;
        this.destintationNameResolver = destintationNameResolver;
        this.executor = actionExecutor;
        this.binder = binder;
    }

    @Override
    public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
        this.endpoints.stream().forEach(endpoint -> {
            LOGGER.atDebug().log("Registering endpoint on destination '{}'", endpoint.getDestinationName());

            final var defaultMsgDispatcher = new DefaultJmsMsgDispatcher(endpoint.getResolver(), binder, executor);
            final var id = endpoint.getDestinationName() + "@" + AtEndpoint.class.getCanonicalName();

            registrar.registerEndpoint(new JmsListenerEndpoint() {

                @Override
                public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
                    final AbstractMessageListenerContainer container = (AbstractMessageListenerContainer) listenerContainer;
                    container.setDestinationName(endpoint.getDestinationName());
                    container.setDestinationResolver((session, destinationName,
                            pubSubDomain) -> destintationNameResolver.resolve("", destinationName));
                    container.setupMessageListener((MessageListener) message -> {
                        final var msg = TextJmsMsg.from((TextMessage)message);

                        ThreadContext.put(AufJmsProperties.MSG_TYPE, msg.type());
                        ThreadContext.put(AufJmsProperties.CORRELATION_ID, msg.correlationId());

                        defaultMsgDispatcher.dispatch(msg);

                        ThreadContext.remove(AufJmsProperties.MSG_TYPE);
                        ThreadContext.remove(AufJmsProperties.CORRELATION_ID);
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
