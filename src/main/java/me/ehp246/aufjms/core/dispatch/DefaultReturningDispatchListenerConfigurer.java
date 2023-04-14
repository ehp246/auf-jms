package me.ehp246.aufjms.core.dispatch;

import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import jakarta.jms.Session;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.core.inbound.DefaultInboundMessageListener;
import me.ehp246.aufjms.core.inbound.DefaultInvocableDispatcher;

/**
 * @author Lei Yang
 *
 */
final class DefaultReturningDispatchListenerConfigurer implements JmsListenerConfigurer {
    private final ConnectionFactoryProvider cfProvider;
    private final EnableByJmsConfig byJmsConfig;
    private final ReturningDispatcheRepo returningDispatcheRepo;

    public DefaultReturningDispatchListenerConfigurer(final ConnectionFactoryProvider cfProvider,
            final EnableByJmsConfig byJmsConfig, final ReturningDispatcheRepo returningDispatcheRepo) {
        super();
        this.cfProvider = cfProvider;
        this.byJmsConfig = byJmsConfig;
        this.returningDispatcheRepo = returningDispatcheRepo;
    }

    @Override
    public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
        final var returnsAt = byJmsConfig.returnsAt();
        if (returnsAt == null) {
            return;
        }
        final var factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.cfProvider.get(""));
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);

        registrar.registerEndpoint(new JmsListenerEndpoint() {
            @Override
            public void setupListenerContainer(final MessageListenerContainer listenerContainer) {
                final var container = (AbstractMessageListenerContainer) listenerContainer;

                container.setBeanName("");
                container.setDestinationName(returnsAt.value());
                container.setPubSubDomain(returnsAt.type() == DestinationType.TOPIC);

                container.setupMessageListener(new DefaultInboundMessageListener(
                        new DefaultInvocableDispatcher(), msg -> returningDispatcheRepo.take(msg.correlationId())));
            }

            @Override
            public String getId() {
                return "";
            }

        }, factory);
    }

}
