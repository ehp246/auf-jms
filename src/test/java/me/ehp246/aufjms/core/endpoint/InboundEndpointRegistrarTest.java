package me.ehp246.aufjms.core.endpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.core.endpoint.AtEndpointRegistrarTestCases.InboundConfig01;

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
                registry.getBeanDefinition("96df151f-e6aa-419a-ab38-8de1a28c1d2e").getBeanClassName());
    }
}
