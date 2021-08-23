package me.ehp246.aufjms.core.endpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
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
                registry.getBeanDefinition("QUEUE://").getBeanClassName());
    }

    @Test
    void name_02() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig02.class),
                registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("QUEUE://queue.1").getBeanClassName());
    }

    @Test
    void name_03() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(InboundConfig03.class),
                registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("atEndpoint.2").getBeanClassName());
    }

}
