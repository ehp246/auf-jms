package me.ehp246.aufjms.core.endpoint;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.endpoint.ExecutorProvider;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.Invoked;
import me.ehp246.aufjms.api.endpoint.MsgConsumer;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.endpoint.MsgInvocableFactory;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JMSSupplier;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.util.TextJmsMsg;

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

    public InboundEndpointListenerConfigurer(final ConnectionFactoryProvider cfProvider,
            final Set<InboundEndpoint> endpoints, final ExecutorProvider executorProvider, final InvocableBinder binder,
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

        for (final var endpoint : this.endpoints) {
            LOGGER.atTrace().log("Registering '{}' endpoint on '{}'", endpoint.name(), endpoint.from().on());

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

                    container.setupMessageListener(new SessionAwareMessageListener<Message>() {
                        private final static Logger logger = LogManager.getLogger(InboundEndpoint.class);

                        private final InvocableDispatcher dispatcher = new InvocableDispatcher(binder, Invoked::invoke,
                                Arrays.asList(new ReplyInvoked(dispathFnProvider.get(endpoint.connectionFactory())),
                                        endpoint.invocationListener()),
                                executorProvider.get(endpoint.concurrency()));
                        private final MsgInvocableFactory invocableFactory = endpoint.invocableFactory();
                        private final MsgConsumer defaultConsumer = endpoint.defaultConsumer();

                        @Override
                        public void onMessage(Message message, Session session) throws JMSException {
                            if (!(message instanceof TextMessage textMessage)) {
                                throw new IllegalArgumentException("Un-supported message type");
                            }

                            final var msg = TextJmsMsg.from(textMessage);

                            try {
                                AufJmsContext.set(session);
                                Log4jContext.set(msg);

                                logger.atTrace().log("Consuming {}", msg::correlationId);

                                final var invocable = invocableFactory.get(msg);

                                if (invocable == null) {
                                    if (defaultConsumer == null) {
                                        throw new UnknownTypeException(msg);
                                    } else {
                                        defaultConsumer.accept(msg);
                                        return;
                                    }
                                }

                                final var msgCtx = new MsgContext() {

                                    @Override
                                    public JmsMsg msg() {
                                        return msg;
                                    }

                                    @Override
                                    public Session session() {
                                        return session;
                                    }

                                };

                                logger.atTrace().log("Dispatching {}", () -> invocable.method().toString());

                                dispatcher.dispatch(invocable, msgCtx);

                                logger.atTrace().log("Consumed {}", msg::correlationId);
                            } catch (Exception e) {
                                logger.atError().withThrowable(e).log("Message failed: {}", e.getMessage());

                                throw e;
                            } finally {
                                AufJmsContext.clearSession();
                                Log4jContext.clear();
                            }
                        }
                    });
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
