package me.ehp246.test.embedded.inbound.listenercontainer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "startup=false", "selector=JMSPriority = 9" })
class ListenerContainerTest {
    @Autowired
    private ApplicationContext appCtx;

    @Test
    void autostartup_001() {
        Assertions.assertEquals(false,
                appCtx.getBean(JmsListenerEndpointRegistry.class).getListenerContainer("startup1").isAutoStartup());
    }

    @Test
    void autostartup_002() {
        Assertions.assertEquals(true,
                appCtx.getBean(JmsListenerEndpointRegistry.class).getListenerContainer("startup2").isAutoStartup());
    }

    @Test
    void autostartup_003() {
        Assertions.assertEquals(false,
                appCtx.getBean(JmsListenerEndpointRegistry.class).getListenerContainer("startup3").isAutoStartup());
    }

    @Test
    void selector_01() {
        final var listenerContainer = (AbstractMessageListenerContainer) appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("selector1");

        Assertions.assertEquals("JMSPriority = 1", listenerContainer.getMessageSelector());
    }

    @Test
    void selector_02() {
        final var listenerContainer = (AbstractMessageListenerContainer) appCtx
                .getBean(JmsListenerEndpointRegistry.class).getListenerContainer("selector2");

        Assertions.assertEquals("JMSPriority = 9", listenerContainer.getMessageSelector(),
                "should support Spring property.");
    }
}
