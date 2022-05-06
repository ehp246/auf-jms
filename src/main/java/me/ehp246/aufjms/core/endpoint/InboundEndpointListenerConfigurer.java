package me.ehp246.aufjms.core.endpoint;

import java.util.Objects;
import java.util.Set;

import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.ExecutorProvider;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JMSSupplier;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * JmsListenerConfigurer used to register {@link InboundEndpoint}'s at run-time.
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundEndpointListenerConfigurer implements JmsListenerConfigurer {
    private final static Logger LOGGER = LogManager.getLogger(InboundEndpointListenerConfigurer.class);

    private final Set<InboundEndpoint> endpoints;
    private final ExecutorProvider executorProvider;
    private final InvocableBinder binder;
    private final ConnectionFactoryProvider cfProvider;
    private final JmsDispatchFnProvider dispathFnProvider;

    public InboundEndpointListenerConfigurer(final ConnectionFactoryProvider cfProvider,
            final Set<InboundEndpoint> endpoints, final ExecutorProvider executorProvider,
            final InvocableBinder binder, final JmsDispatchFnProvider dispathFnProvider) {
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

        for (final var endpoint : this.endpoints) {
            LOGGER.atTrace().log("Registering '{}' endpoint on '{}'", endpoint.name(), endpoint.from().on());

            final var dispatcher = new InboundMsgConsumer(endpoint.resolver(), binder, InvocationOutcome::invoke,
                    executorProvider.get(endpoint.concurrency()),
                    this.dispathFnProvider.get(endpoint.connectionFactory()),
                    new InvocationListenersSupplier(endpoint.completedInvocationListener(),
                            endpoint.failedInvocationInterceptor()));

            registrar.registerEndpoint(new JmsListenerEndpoint() {

                @Override
                public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
                    final var container = (AbstractMessageListenerContainer) listenerContainer;
                    final var from = endpoint.from();
                    final var on = from.on();

                    container.setBeanName(endpoint.name());
                    container.setAutoStartup(endpoint.autoStartup());
                    container.setMessageSelector(from.selector());
                    container.setDestinationName(on.name());

                    if (on instanceof AtTopic) {
                        final var sub = from.sub();
                        container.setSubscriptionName(sub.name());
                        container.setSubscriptionDurable(sub.durable());
                        container.setSubscriptionShared(sub.shared());
                    }

                    container.setDestinationResolver((session, name, topic) -> JMSSupplier
                            .invoke(() -> on instanceof AtTopic ? session.createTopic(on.name())
                                    : session.createQueue(on.name())));

                    container.setupMessageListener(dispatcher);
                }

                @Override
                public String getId() {
                    return endpoint.name();
                }

            }, listenerContainerFactory);
        }
    }

    private DefaultJmsListenerContainerFactory jmsListenerContainerFactory(final String cfName) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.cfProvider.get(cfName));
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
}
