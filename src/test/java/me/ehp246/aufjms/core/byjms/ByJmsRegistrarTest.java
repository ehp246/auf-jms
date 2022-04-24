package me.ehp246.aufjms.core.byjms;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.core.byjms.registrar.case01.RegistrarCase01;
import me.ehp246.aufjms.core.byjms.registrar.case02.RegistrarCase02;
import me.ehp246.aufjms.core.dispatch.ByJmsRegistrar;
import me.ehp246.aufjms.core.dispatch.EnableByJmsConfig;

/**
 * @author Lei Yang
 *
 */
class ByJmsRegistrarTest {

    @Test
    void scan_01() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(RegistrarAppConfigs.Config01.class),
                registry);

        Assertions.assertEquals(2, registry.getBeanDefinitionCount());
    }

    @Test
    void scan_02() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(RegistrarAppConfigs.Config02.class),
                registry);

        Assertions.assertEquals(1, registry.getBeanDefinitionCount());
        Assertions.assertEquals(RegistrarCase01.class.getSimpleName(), registry.getBeanDefinitionNames()[0]);
    }

    @Test
    void scan_03() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(RegistrarAppConfigs.Config03.class),
                registry);

        Assertions.assertEquals("8c9abb70-7ed0-40ec-9c2d-eb408a2feb09", registry.getBeanDefinitionNames()[0]);
    }

    @Test
    void ttl_01() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(RegistrarAppConfigs.TtlConfig01.class), registry);

        Assertions.assertEquals("PT0.11S",
                ((EnableByJmsConfig) (registry.getBeanDefinition(RegistrarCase01.class.getSimpleName())
                        .getConstructorArgumentValues().getArgumentValue(0, EnableByJmsConfig.class).getValue()))
                                .ttl());
    }

    @Test
    void ttl_02() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(RegistrarAppConfigs.TtlConfig02.class), registry);

        Assertions.assertEquals("PT0.112S",
                ((EnableByJmsConfig) (registry.getBeanDefinition(RegistrarCase02.NAME)
                        .getConstructorArgumentValues().getArgumentValue(0, EnableByJmsConfig.class).getValue()))
                                .ttl());
    }

    @Test
    void dispatchFn_01() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(RegistrarAppConfigs.DispatchFnConfig01.class), registry);

        Assertions.assertEquals(0, Arrays.asList(registry.getBeanDefinitionNames()).stream().filter(
                name -> registry.getBeanDefinition(name).getBeanClassName().equals(JmsDispatchFn.class.getName()))
                .count());
    }

    @Test
    void dispatchFn_02() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(RegistrarAppConfigs.DispatchFnConfig02.class), registry);

        Assertions.assertEquals(JmsDispatchFn.class.getName(),
                registry.getBeanDefinition("JmsDispatchFn-0").getBeanClassName());

        Assertions.assertEquals(1, Arrays.asList(registry.getBeanDefinitionNames()).stream().filter(
                name -> registry.getBeanDefinition(name).getBeanClassName().equals(JmsDispatchFn.class.getName()))
                .count());
    }

    @Test
    void dispatchFn_03() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(
                AnnotationMetadata.introspect(RegistrarAppConfigs.DispatchFnConfig03.class), registry);

        Assertions.assertEquals(JmsDispatchFn.class.getName(),
                registry.getBeanDefinition("JmsDispatchFn-0").getBeanClassName());
        Assertions.assertEquals(JmsDispatchFn.class.getName(),
                registry.getBeanDefinition("JmsDispatchFn-1").getBeanClassName());

        Assertions.assertEquals(2, Arrays.asList(registry.getBeanDefinitionNames()).stream().filter(
                name -> registry.getBeanDefinition(name).getBeanClassName().equals(JmsDispatchFn.class.getName()))
                .count());
    }
}
