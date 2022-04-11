package me.ehp246.aufjms.integration.endpoint.name;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import me.ehp246.aufjms.api.endpoint.InboundEndpoint;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {})
class BeanNameTest {
    @Autowired
    private ApplicationContext appCtx;

    @Test
    void name_01() {
        Assertions.assertEquals(3, appCtx.getBeansOfType(InboundEndpoint.class).size());
    }

    @Test
    void name_02() {
        final var bean = appCtx.getBean("a778506e-a1dc-40c6-aeb3-42114f993c22");

        Assertions.assertEquals(true, bean instanceof InboundEndpoint);
        Assertions.assertEquals("a903988f-89af-42ba-9777-f52831b480ff", ((InboundEndpoint) bean).from().on().name());
    }

    @Test
    void name_03() {
        Assertions.assertEquals(true, appCtx.getBean(JmsListenerEndpointRegistry.class)
                .getListenerContainer("a778506e-a1dc-40c6-aeb3-42114f993c22") != null);
    }

}
