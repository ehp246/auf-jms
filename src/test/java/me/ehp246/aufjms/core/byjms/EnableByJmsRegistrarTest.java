package me.ehp246.aufjms.core.byjms;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.core.dispatch.EnableByJmsRegistrar;

/**
 * @author Lei Yang
 *
 */
class EnableByJmsRegistrarTest {
    @Test
    void scan_03() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new EnableByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AppConfigs.Config03.class),
                registry);

        Assertions.assertEquals("8c9abb70-7ed0-40ec-9c2d-eb408a2feb09", registry.getBeanDefinitionNames()[0]);
    }

    @Test
    void dispatchFn_01() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new EnableByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(AppConfigs.DispatchFnConfig01.class), registry);

        Assertions.assertEquals(0, Arrays.asList(registry.getBeanDefinitionNames()).stream().filter(
                name -> registry.getBeanDefinition(name).getBeanClassName().equals(JmsDispatchFn.class.getName()))
                .count());
    }

    @Test
    void dispatchFn_02() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new EnableByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(AppConfigs.DispatchFnConfig02.class), registry);

        Assertions.assertEquals(JmsDispatchFn.class.getName(),
                registry.getBeanDefinition("jmsDispatchFn-0").getBeanClassName());

        Assertions.assertEquals(1, Arrays.asList(registry.getBeanDefinitionNames()).stream().filter(
                name -> registry.getBeanDefinition(name).getBeanClassName().equals(JmsDispatchFn.class.getName()))
                .count());
    }

    @Test
    void dispatchFn_03() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new EnableByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(AppConfigs.DispatchFnConfig03.class), registry);

        Assertions.assertEquals(JmsDispatchFn.class.getName(),
                registry.getBeanDefinition("jmsDispatchFn-0").getBeanClassName());
        Assertions.assertEquals(JmsDispatchFn.class.getName(),
                registry.getBeanDefinition("jmsDispatchFn-1").getBeanClassName());

        Assertions.assertEquals(2, Arrays.asList(registry.getBeanDefinitionNames()).stream().filter(
                name -> registry.getBeanDefinition(name).getBeanClassName().equals(JmsDispatchFn.class.getName()))
                .count());
    }
}
