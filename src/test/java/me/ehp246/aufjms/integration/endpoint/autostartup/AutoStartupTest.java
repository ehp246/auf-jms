package me.ehp246.aufjms.integration.endpoint.autostartup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "startup=false" })
public class AutoStartupTest {
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
}
