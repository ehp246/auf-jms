package me.ehp246.aufjms.core.endpoint;

import java.util.Objects;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.support.destination.DestinationResolver;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutorProvider;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.core.jms.AtDestinationRecord;

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
    private final ConnectionFactoryProvider cfProvider;
    private final JmsDispatchFnProvider dispathFnProvider;

    public InboundListenerConfigurer(final ConnectionFactoryProvider cfProvider, final Set<InboundEndpoint> endpoints,
            final ExecutorProvider executorProvider, final ExecutableBinder binder,
            final JmsDispatchFnProvider dispathFnProvider) {
        super();
        this.cfProvider = Objects.requireNonNull(cfProvider);
        this.endpoints = endpoints;
        this.executorProvider = executorProvider;
        this.binder = binder;
        this.dispathFnProvider = dispathFnProvider;
    }

    @Override
    public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
        final var listenerContainerFactory = jmsListenerContainerFactory(null);

        this.endpoints.stream().forEach(endpoint -> {
            LOGGER.atDebug().log("Registering '{}' endpoint at {} on {}", endpoint.name(), endpoint.from().name(),
                    endpoint.connectionFactory());

            final var dispatcher = new DefaultMsgDispatcher(endpoint.resolver(), binder,
                    executorProvider.get(endpoint.concurrency()),
                    this.dispathFnProvider.get(endpoint.connectionFactory()), endpoint.failedInvocationConsumer());

            registrar.registerEndpoint(new JmsListenerEndpoint() {

                @Override
                public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
                    final var container = (AbstractMessageListenerContainer) listenerContainer;
                    final var from = endpoint.from();

                    container.setBeanName(endpoint.name());
                    container.setAutoStartup(endpoint.autoStartup());
                    container.setMessageSelector(from.selector());
                    container.setDestinationName(from.name());

                    if (from.type() == DestinationType.TOPIC) {
                        final var sub = from.sub();
                        container.setSubscriptionName(sub.name());
                        container.setSubscriptionDurable(sub.durable());
                        container.setSubscriptionShared(sub.shared());
                    }

                    container.setDestinationResolver(new DestinationResolver() {
                        private final AtDestinationRecord at = new AtDestinationRecord(from.name(), from.type());

                        @Override
                        public Destination resolveDestinationName(Session session, String name, boolean topic)
                                throws JMSException {
                            return at.jmsDestination(session);
                        }
                    });

                    container.setupMessageListener(dispatcher);
                }

                @Override
                public String getId() {
                    return endpoint.name();
                }
                
            }, listenerContainerFactory);
        });
    }

    private DefaultJmsListenerContainerFactory jmsListenerContainerFactory(final String cfName) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.cfProvider.get(cfName));
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
}
