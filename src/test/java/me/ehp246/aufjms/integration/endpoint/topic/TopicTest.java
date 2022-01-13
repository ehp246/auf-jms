package me.ehp246.aufjms.integration.endpoint.topic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

import me.ehp246.aufjms.util.TestTopicListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "" })
public class TopicTest {
    @Autowired
    private ApplicationContext appCtx;

    @Test
    public void sub_001() {
        final var abstractMessageListenerContainer = (AbstractMessageListenerContainer) (appCtx.getBean(JmsListenerEndpointRegistry.class)
                .getListenerContainer("sub1"));

        Assertions.assertEquals(TestTopicListener.SUBSCRIPTION_NAME,
                abstractMessageListenerContainer.getSubscriptionName());
        Assertions.assertEquals(true, abstractMessageListenerContainer.isPubSubDomain());
        Assertions.assertEquals(true, abstractMessageListenerContainer.isSubscriptionDurable());
        Assertions.assertEquals(true, abstractMessageListenerContainer.isSubscriptionShared());
    }

    @Test
    public void sub_002() {
        final var abstractMessageListenerContainer = (AbstractMessageListenerContainer) (appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("sub2"));

        Assertions.assertEquals("",
                abstractMessageListenerContainer.getSubscriptionName());

        Assertions.assertEquals(false, abstractMessageListenerContainer.isPubSubDomain());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionDurable());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionShared());
    }

    @Test
    public void sub_003() {
        final var abstractMessageListenerContainer = (AbstractMessageListenerContainer) (appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("sub3"));

        Assertions.assertEquals(false, abstractMessageListenerContainer.isPubSubDomain());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionDurable());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionShared());
    }
}
