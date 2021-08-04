package me.ehp246.aufjms.core.byjms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.integration.enablebyjms.case01.ScanCase01;

/**
 * @author Lei Yang
 *
 */
class ByJmsRegistrarTest {

    @Test
    void scan_01() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AppConfigs.Config01.class),
                registry);

        Assertions.assertEquals(0, registry.getBeanDefinitionCount());
    }

    @Test
    void scan_02() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AppConfigs.Config02.class),
                registry);

        Assertions.assertEquals(1, registry.getBeanDefinitionCount());
        Assertions.assertEquals(ScanCase01.class.getSimpleName(), registry.getBeanDefinitionNames()[0]);
    }

    @Test
    void scan_03() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AppConfigs.Config03.class),
                registry);

        Assertions.assertEquals("8c9abb70-7ed0-40ec-9c2d-eb408a2feb09", registry.getBeanDefinitionNames()[0]);
    }

    @Test
    void replyTo_01() {
        final var registry = new SimpleBeanDefinitionRegistry();
        new ByJmsRegistrar().registerBeanDefinitions(AnnotationMetadata.introspect(AppConfigs.ReplyToConfig01.class),
                registry);

        Assertions.assertEquals("From Enabled",
                ((ByJmsProxyConfig) (registry.getBeanDefinition(ScanCase01.class.getSimpleName())
                        .getConstructorArgumentValues().getArgumentValue(1, ByJmsProxyConfig.class).getValue()))
                                .replyTo());
    }
}
