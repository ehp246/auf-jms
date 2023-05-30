package me.ehp246.aufjms.core.dispatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.core.util.TextJmsMsg;

/**
 * @author Lei Yang
 *
 */
final class DispatchReplyListenerConfigurer implements JmsListenerConfigurer {
    private final static Logger LOGGER = LogManager.getLogger();

    private final ConnectionFactoryProvider cfProvider;
    private final EnableByJmsConfig byJmsConfig;
    private final DefaultReplyExpectedDispatchMap defaultReplyExpectedDispatchMap;

    public DispatchReplyListenerConfigurer(final ConnectionFactoryProvider cfProvider,
            final EnableByJmsConfig byJmsConfig, final DefaultReplyExpectedDispatchMap defaultReplyExpectedDispatchMap) {
        super();
        this.cfProvider = cfProvider;
        this.byJmsConfig = byJmsConfig;
        this.defaultReplyExpectedDispatchMap = defaultReplyExpectedDispatchMap;
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

                container.setupMessageListener(new SessionAwareMessageListener<Message>() {

                    @Override
                    public void onMessage(final Message message, final Session session) throws JMSException {
                        if (!(message instanceof final TextMessage textMessage)) {
                            throw new IllegalArgumentException("Un-supported message type");
                        }

                        final var msg = TextJmsMsg.from(textMessage);

                        LOGGER.atDebug().log("Reply to correlation Id: {}, type: {}", msg::correlationId, msg::type);
                        LOGGER.atTrace().log("Body: {}", msg::text);

                        defaultReplyExpectedDispatchMap.get(msg.correlationId()).complete(msg);
                    }
                });
            }

            @Override
            public String getId() {
                return DispatchReplyListenerConfigurer.class.getSimpleName();
            }

        }, factory);
    }

}
