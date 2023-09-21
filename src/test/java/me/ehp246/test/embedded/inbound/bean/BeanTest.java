package me.ehp246.test.embedded.inbound.bean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.ErrorHandler;

import jakarta.jms.Session;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.core.inbound.NoopConsumer;
import me.ehp246.test.embedded.inbound.bean.AppConfigs.AppConfig03;

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

        Assertions.assertEquals(2, appCtx.getBeansOfType(InboundEndpoint.class).size());

        final var registry = appCtx.getBean(JmsListenerEndpointRegistry.class);

        final var container01 = (DefaultMessageListenerContainer) registry.getListenerContainer("endpoint01");

        Assertions.assertEquals(true, container01.isAutoStartup());
        Assertions.assertEquals(false, container01.isPubSubDomain());
        Assertions.assertEquals(true, container01.isSessionTransacted());

        final var container02 = (DefaultMessageListenerContainer) registry.getListenerContainer("endpoint02");

        Assertions.assertEquals(false, container02.isSessionTransacted());
        Assertions.assertEquals(Session.DUPS_OK_ACKNOWLEDGE, container02.getSessionAcknowledgeMode());
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

    @Test
    void session_01() throws BeansException, InterruptedException, ExecutionException {
        appCtx.register(AppConfigs.AppConfig05.class);
        appCtx.refresh();

        final var endpoint = appCtx.getBean(InboundEndpoint.class);

        Assertions.assertEquals(0, endpoint.sessionMode());
    }

    @Test
    void session_02() throws BeansException, InterruptedException, ExecutionException {
        appCtx.register(AppConfigs.AppConfig06.class);
        appCtx.refresh();

        final var endpoint = appCtx.getBean(InboundEndpoint.class);

        Assertions.assertEquals(Session.CLIENT_ACKNOWLEDGE, endpoint.sessionMode());
    }

    @Test
    void errorHandler_01() throws BeansException, InterruptedException, ExecutionException {
        appCtx.register(AppConfigs.AppConfig07.class);
        appCtx.refresh();

        final var endpoint = appCtx.getBean(InboundEndpoint.class);

        Assertions.assertEquals(null, endpoint.errorHandler());
    }

    @Test
    void errorHandler_02() throws BeansException, InterruptedException, ExecutionException {
        appCtx.setEnvironment(new MockEnvironment().withProperty("error.handler", "none"));
        appCtx.register(AppConfigs.AppConfig07.class);

        Assertions.assertThrows(UnsatisfiedDependencyException.class, appCtx::refresh);
    }

    @Test
    void errorHandler_03() throws BeansException, InterruptedException, ExecutionException {
        appCtx.setEnvironment(new MockEnvironment().withProperty("error.handler", "thisHandler"));
        appCtx.register(AppConfigs.AppConfig07.class);
        appCtx.refresh();

        Assertions.assertEquals(appCtx.getBean(ErrorHandler.class),
                appCtx.getBean(InboundEndpoint.class).errorHandler());
    }
}
