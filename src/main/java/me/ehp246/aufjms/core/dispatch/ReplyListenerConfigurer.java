package me.ehp246.aufjms.core.dispatch;

import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import jakarta.jms.Session;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;

/**
 * @author Lei Yang
 *
 */
final class ReplyListenerConfigurer implements JmsListenerConfigurer {
    private final ConnectionFactoryProvider cfProvider;
    private final EnableByJmsConfig byJmsConfig;
    private final ReplyFutureSupplier futureSupplier;

    public ReplyListenerConfigurer(final ConnectionFactoryProvider cfProvider,
            final EnableByJmsConfig byJmsConfig, final ReplyFutureSupplier futureSupplier) {
        super();
        this.cfProvider = cfProvider;
        this.byJmsConfig = byJmsConfig;
        this.futureSupplier = futureSupplier;
    }

    @Override
    public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
        final var dispatchReplyAt = byJmsConfig.requestReplyAt();
        if (dispatchReplyAt == null) {
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
                container.setDestinationName(dispatchReplyAt.name());
                container.setPubSubDomain(dispatchReplyAt instanceof AtTopic);

                container.setupMessageListener(new ReplyListener(futureSupplier));
            }

            @Override
            public String getId() {
                return ReplyListener.class.getSimpleName() + "@" + dispatchReplyAt;
            }

        }, factory);
    }

}
