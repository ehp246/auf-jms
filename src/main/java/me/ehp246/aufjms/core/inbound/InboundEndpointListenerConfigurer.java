package me.ehp246.aufjms.core.inbound;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import jakarta.jms.Session;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.inbound.ExecutorProvider;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.InvocableBinder;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * JmsListenerConfigurer used to register {@link InboundEndpoint}'s at run-time.
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundEndpointListenerConfigurer implements JmsListenerConfigurer {
    final static Logger LOGGER = LogManager.getLogger(InboundEndpointListenerConfigurer.class);

    private final Set<InboundEndpoint> endpoints;
    private final ExecutorProvider executorProvider;
    private final InvocableBinder binder;
    private final ConnectionFactoryProvider cfProvider;
    private final JmsDispatchFnProvider dispathFnProvider;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public InboundEndpointListenerConfigurer(final ConnectionFactoryProvider cfProvider,
            final Set<InboundEndpoint> endpoints, final ExecutorProvider executorProvider, final InvocableBinder binder,
            final JmsDispatchFnProvider dispathFnProvider,
            final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        super();
        this.cfProvider = Objects.requireNonNull(cfProvider);
        this.endpoints = endpoints;
        this.executorProvider = executorProvider;
        this.binder = binder;
        this.dispathFnProvider = dispathFnProvider;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    @Override
    public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
        for (final var endpoint : this.endpoints) {
            LOGGER.atTrace().log("Registering '{}' on '{}', '{}'", endpoint::name, () -> endpoint.from().on(),
                    () -> endpoint.from().sub());

            final var factory = new DefaultJmsListenerContainerFactory();
            factory.setConnectionFactory(this.cfProvider.get(endpoint.connectionFactory()));
            factory.setSessionTransacted(endpoint.sessionMode() == Session.SESSION_TRANSACTED);
            factory.setSessionAcknowledgeMode(endpoint.sessionMode());
            factory.setExceptionListener(endpoint.exceptionListener());
            factory.setErrorHandler(endpoint.errorHandler());

            registrar.registerEndpoint(new JmsListenerEndpoint() {
                @Override
                public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
                    final var container = (AbstractMessageListenerContainer) listenerContainer;
                    final var from = endpoint.from();
                    final var on = from.on();

                    container.setBeanName(OneUtil.firstUpper(endpoint.name()) + "@" + on);
                    container.setAutoStartup(endpoint.autoStartup());
                    container.setMessageSelector(from.selector());
                    container.setDestinationName(on.name());
                    container.setPubSubDomain(on instanceof AtTopic);

                    if (on instanceof AtTopic) {
                        final var sub = from.sub();
                        container.setSubscriptionName(sub.name());
                        container.setSubscriptionDurable(sub.durable());
                        container.setSubscriptionShared(sub.shared());
                    }

                    container.setupMessageListener(new DefaultInboundMessageListener(
                            new DefaultInvocableDispatcher(binder,
                                    Arrays.asList(new ReplyInvoked(dispathFnProvider.get(endpoint.connectionFactory())),
                                            endpoint.invocationListener()),
                                    executorProvider.get(endpoint.concurrency())),
                            new AutowireCapableInvocableFactory(autowireCapableBeanFactory, endpoint.typeRegistry()),
                            endpoint.defaultConsumer()));
                }

                @Override
                public String getId() {
                    return endpoint.name();
                }

            }, factory);
        }
    }
}
