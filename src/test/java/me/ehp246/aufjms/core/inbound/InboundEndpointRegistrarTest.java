package me.ehp246.aufjms.core.inbound;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.core.inbound.InboundEndpointRegistrar;
import me.ehp246.aufjms.core.inbound.TestCases.Config01;
import me.ehp246.aufjms.core.inbound.TestCases.Config02;
import me.ehp246.aufjms.core.inbound.TestCases.Config03;
import me.ehp246.aufjms.core.inbound.TestCases.Config04;

/**
 * @author Lei Yang
 *
 */
class InboundEndpointRegistrarTest {
    @Test
    void name_01() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(Config01.class),
                registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("96df151f-e6aa-419a-ab38-8de1a28c1d2e").getBeanClassName());
    }

    @Test
    void name_02() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(Config02.class), registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("inboundEndpoint-0").getBeanClassName());
    }

    @Test
    void name_03() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new InboundEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(Config03.class), registry);

        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("inboundEndpoint-0").getBeanClassName());
        Assertions.assertEquals(InboundEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("atEndpoint.2").getBeanClassName());
    }

    @Test
    void name_04() {
        final var registry = new SimpleBeanDefinitionRegistry();

        Assertions.assertThrows(BeanDefinitionOverrideException.class,
                () -> new InboundEndpointRegistrar()
                        .registerBeanDefinitions(AnnotationMetadata.introspect(Config04.class), registry));
    }
}
