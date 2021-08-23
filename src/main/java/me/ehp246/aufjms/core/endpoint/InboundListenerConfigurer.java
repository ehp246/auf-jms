package me.ehp246.aufjms.core.endpoint;

import java.util.Objects;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutorProvider;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * JmsListenerConfigurer used to register {@link InboundEndpoint}'s at run-time.
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundListenerConfigurer implements JmsListenerConfigurer {
    private final static Logger LOGGER = LogManager.getLogger(InboundListenerConfigurer.class);

    private final Set<InboundEndpoint> endpoints;
    private final ExecutorProvider executorProvider;
    private final ExecutableBinder binder;
    private final PropertyResolver propertyResolver;
    private final ConnectionFactory connectionFactory;

    public InboundListenerConfigurer(final ConnectionFactory connectionFactory, final Set<InboundEndpoint> endpoints,
            final ExecutorProvider executorProvider,
            final ExecutableBinder binder, final PropertyResolver propertyResolver) {
        super();
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
        this.endpoints = endpoints;
        this.executorProvider = executorProvider;
        this.binder = binder;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
        final var listenerContainerFactory = jmsListenerContainerFactory();

        this.endpoints.stream().forEach(endpoint -> {
            LOGGER.atDebug().log("Registering endpoint on destination '{}'", endpoint.destination());

            final var dispatcher = new DefaultInvokableDispatcher(endpoint.resolver(), binder,
                    executorProvider.get(Integer.parseInt(this.propertyResolver.resolve(endpoint.concurrency()))));

            registrar.registerEndpoint(new JmsListenerEndpoint() {

                @Override
                public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
                    final var container = (AbstractMessageListenerContainer) listenerContainer;
                    container.setDestination(endpoint.destination());
                    container.setupMessageListener((MessageListener) message -> {
                        final var msg = TextJmsMsg.from((TextMessage) message);

                        ThreadContext.put(AufJmsProperties.MSG_TYPE, msg.type());
                        ThreadContext.put(AufJmsProperties.CORRELATION_ID, msg.correlationId());

                        dispatcher.dispatch(msg);

                        ThreadContext.remove(AufJmsProperties.MSG_TYPE);
                        ThreadContext.remove(AufJmsProperties.CORRELATION_ID);
                    });
                }

                @Override
                public String getId() {
                    return endpoint.name();
                }
            }, listenerContainerFactory);
        });
    }

    private DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.connectionFactory);
        return factory;
    }
}