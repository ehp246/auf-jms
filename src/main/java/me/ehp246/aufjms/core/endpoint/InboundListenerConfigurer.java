package me.ehp246.aufjms.core.endpoint;

import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutorProvider;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.core.jms.AtDestinationRecord;
import me.ehp246.aufjms.core.util.OneUtil;

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
            final ExecutorProvider executorProvider, final ExecutableBinder binder, final JmsDispatchFnProvider dispathFnProvider) {
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
            LOGGER.atDebug().log("Registering '{}' endpoint at {} on {}", endpoint.name(), endpoint.at().name(),
                    endpoint.connectionFactory());

            final var dispatcher = new DefaultInvokableDispatcher(endpoint.resolver(), binder,
                    executorProvider.get(endpoint.concurrency()),
                    this.dispathFnProvider.get(endpoint.connectionFactory()));

            registrar.registerEndpoint(new JmsListenerEndpoint() {

                @Override
                public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
                    final var container = (AbstractMessageListenerContainer) listenerContainer;
                    container.setBeanName(OneUtil.hasValue(endpoint.name()) ? endpoint.name() : endpoint.at().name());
                    container.setAutoStartup(endpoint.autoStartup());
                    container.setDestinationName(endpoint.at().name());
                    if (endpoint.at().type() == DestinationType.TOPIC) {
                        container.setSubscriptionName(endpoint.subscriptionName());
                        container.setSubscriptionDurable(endpoint.durable());
                        container.setSubscriptionShared(endpoint.shared());
                    }
                    container.setDestinationResolver((session, name, topic) -> {
                        return ((AtDestinationRecord) endpoint.at()).jmsDestination(session);
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
        return factory;
    }
}
