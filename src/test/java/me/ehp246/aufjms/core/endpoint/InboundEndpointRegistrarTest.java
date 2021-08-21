package me.ehp246.aufjms.core.endpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.endpoint.AtEndpoint;
import me.ehp246.aufjms.core.endpoint.registrarcases.AtEndpointRegistrarTestCases.AtEndpointConfig01;
import me.ehp246.aufjms.core.endpoint.registrarcases.AtEndpointRegistrarTestCases.AtEndpointConfig02;
import me.ehp246.aufjms.core.endpoint.registrarcases.AtEndpointRegistrarTestCases.AtEndpointConfig03;

/**
 * @author Lei Yang
 *
 */
class AtEndpointRegistrarTest {

    @Test
    void name_01() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new AtEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AtEndpointConfig01.class),
                registry);

        Assertions.assertEquals(AtEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("@").getBeanClassName());
    }

    @Test
    void name_02() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new AtEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AtEndpointConfig02.class),
                registry);

        Assertions.assertEquals(AtEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("queue.1@").getBeanClassName());
    }

    @Test
    void name_03() {
        final var registry = new SimpleBeanDefinitionRegistry();

        new AtEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AtEndpointConfig03.class),
                registry);

        Assertions.assertEquals(AtEndpoint.class.getCanonicalName(),
                registry.getBeanDefinition("atEndpoint.2").getBeanClassName());
    }

    @Test
    void connection_01() {
        final var registry = new GenericApplicationContext();
        registry.registerBean(AtEndpointFactory.class, new Object[] { null });

        new AtEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AtEndpointConfig01.class),
                registry);

        registry.refresh();

        Assertions.assertEquals("", registry.getBean(AtEndpoint.class).connection());
    }

    @Test
    void connection_02() {
        final var registry = new GenericApplicationContext();
        registry.registerBean(AtEndpointFactory.class, new Object[] { null });

        new AtEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AtEndpointConfig03.class),
                registry);

        registry.refresh();

        Assertions.assertEquals("", registry.getBean("queue.1@", AtEndpoint.class).connection());
    }

    @Test
    void connection_03() {
        final var registry = new GenericApplicationContext();
        registry.registerBean(AtEndpointFactory.class, new Object[] { null });

        new AtEndpointRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AtEndpointConfig03.class),
                registry);

        registry.refresh();

        Assertions.assertEquals("connection.2",
                registry.getBean("atEndpoint.2", AtEndpoint.class).connection());
    }
}
