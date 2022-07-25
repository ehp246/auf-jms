package me.ehp246.aufjms.integration.endpoint.bean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.core.endpoint.NoopConsumer;
import me.ehp246.aufjms.integration.endpoint.bean.AppConfigs.AppConfig03;

/**
 * @author Lei Yang
 *
 */
class BeanTest {
    private final AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext();

    @AfterEach
    void tearDown() {
        appCtx.close();
    }

    @Test
    void bean_01() {
        appCtx.register(AppConfigs.AppConfig01.class);
        appCtx.refresh();

        Assertions.assertEquals(1, appCtx.getBeansOfType(InboundEndpoint.class).size());

        final var registry = appCtx.getBean(JmsListenerEndpointRegistry.class);

        final var listenerContainer = registry.getListenerContainer(AppConfigs.AppConfig01.NAME);

        Assertions.assertEquals(true, listenerContainer.isAutoStartup());
        Assertions.assertEquals(false, listenerContainer.isPubSubDomain());
    }

    @Test
    void bean_02() {
        appCtx.register(AppConfigs.AppConfig02.class);
        appCtx.refresh();

        Assertions.assertEquals(1, appCtx.getBeansOfType(InboundEndpoint.class).size());

        final var registry = appCtx.getBean(JmsListenerEndpointRegistry.class);

        final var listenerContainer = registry.getListenerContainer(AppConfigs.AppConfig02.NAME);

        Assertions.assertEquals(false, listenerContainer.isAutoStartup());
        Assertions.assertEquals(true, listenerContainer.isPubSubDomain());
    }

    @Test
    void bean_03() throws BeansException, InterruptedException, ExecutionException {
        appCtx.register(AppConfigs.AppConfig03.class);
        appCtx.refresh();

        appCtx.getBean(AppConfig03.Send.class).send();

        Assertions.assertEquals(true, appCtx.getBean(CompletableFuture.class).get(), "should close");

        final var endpoint = appCtx.getBean(InboundEndpoint.class);

        Assertions.assertEquals(appCtx.getBean(NoopConsumer.class), endpoint.defaultConsumer());
    }

    @Test
    void defaultConsumer_01() throws BeansException, InterruptedException, ExecutionException {
        appCtx.register(AppConfigs.AppConfig04.class);
        appCtx.refresh();

        final var endpoint = appCtx.getBean(InboundEndpoint.class);

        Assertions.assertEquals(null, endpoint.defaultConsumer());
    }
}
