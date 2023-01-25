package me.ehp246.test.embedded.endpoint.topic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

import me.ehp246.test.TestTopicListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "sub-name=sub-4" })
public class TopicTest {
    @Autowired
    private ApplicationContext appCtx;

    @Test
    public void sub_01() {
        final var abstractMessageListenerContainer = (AbstractMessageListenerContainer) (appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("sub1"));

        Assertions.assertEquals(TestTopicListener.SUBSCRIPTION_NAME,
                abstractMessageListenerContainer.getSubscriptionName());

        Assertions.assertEquals(true, abstractMessageListenerContainer.isPubSubDomain());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionDurable());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionShared());
    }

    @Test
    public void sub_02() {
        final var abstractMessageListenerContainer = (AbstractMessageListenerContainer) (appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("inboundEndpoint-1"));

        Assertions.assertEquals(true, abstractMessageListenerContainer.isPubSubDomain());

        Assertions.assertEquals("", abstractMessageListenerContainer.getSubscriptionName());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionDurable());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionShared());
    }

    @Test
    public void sub_03() {
        final var abstractMessageListenerContainer = (AbstractMessageListenerContainer) (appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("sub3"));

        Assertions.assertEquals(false, abstractMessageListenerContainer.isPubSubDomain());

        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionDurable());
        Assertions.assertEquals(false, abstractMessageListenerContainer.isSubscriptionShared());
    }

    @Test
    public void sub_04() {
        final var abstractMessageListenerContainer = (AbstractMessageListenerContainer) (appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("sub4"));

        Assertions.assertEquals(true, abstractMessageListenerContainer.isPubSubDomain());
        Assertions.assertEquals("sub-4", abstractMessageListenerContainer.getSubscriptionName(),
                "Should be from the property.");
        Assertions.assertEquals(true, abstractMessageListenerContainer.isSubscriptionDurable());
        Assertions.assertEquals(true, abstractMessageListenerContainer.isSubscriptionShared());
    }
}
