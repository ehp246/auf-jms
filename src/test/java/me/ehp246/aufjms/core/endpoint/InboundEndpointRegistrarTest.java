package me.ehp246.aufjms.core.endpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.core.endpoint.registrarcases.AtEndpointRegistrarTestCases.InboundConfig01;
import me.ehp246.aufjms.core.endpoint.registrarcases.AtEndpointRegistrarTestCases.InboundConfig02;
import me.ehp246.aufjms.core.endpoint.registrarcases.AtEndpointRegistrarTestCases.InboundConfig03;

/**
 * @author Lei Yang
 *
 */
class InboundEndpointRegistrarTest {

    @Test
    void name_01() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig01.class),
                registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("QUEUE://@").getBeanClassName());
    }

    @Test
    void name_02() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig02.class),
                registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("QUEUE://queue.1@").getBeanClassName());
    }

    @Test
    void name_03() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig03.class),
                registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("atEndpoint.2").getBeanClassName());
    }

    @Test
    void context_01() {
        final var registry = new GenericApplicationContext();
        registry.registerBean(InboundEndpointFactory.class, new Object[] { null });

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig01.class),
                registry);

        registry.refresh();

        Assertions.assertEquals("", registry.getBean(InboundEndpoint.class).context());
    }

    @Test
    void context_02() {
        final var registry = new GenericApplicationContext();
        registry.registerBean(InboundEndpointFactory.class, new Object[] { null });

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig03.class),
                registry);

        registry.refresh();

        Assertions.assertEquals("", registry.getBean("QUEUE://queue.1@", InboundEndpoint.class).context());
    }

    @Test
    void context_03() {
        final var registry = new GenericApplicationContext();
        registry.registerBean(InboundEndpointFactory.class, new Object[] { null });

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig03.class),
                registry);

        registry.refresh();

        Assertions.assertEquals("connection.2",
                registry.getBean("atEndpoint.2", InboundEndpoint.class).context());
    }
}
