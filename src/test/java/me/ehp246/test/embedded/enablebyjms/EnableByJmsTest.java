package me.ehp246.test.embedded.enablebyjms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.test.embedded.enablebyjms.case01.AppConfig01;
import me.ehp246.test.embedded.enablebyjms.case01.Case01;
import me.ehp246.test.embedded.enablebyjms.case02.AppConfig02;
import me.ehp246.test.embedded.enablebyjms.case02.Case02;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class EnableByJmsTest {
    @Test
    void appConfig_01() {
        final var config = new AnnotationConfigApplicationContext(AppConfig01.class).getBean(EnableByJmsConfig.class);

        Assertions.assertEquals(0, config.scan().size());
        Assertions.assertEquals(null, config.ttl());
        Assertions.assertEquals(0, config.dispatchFns().size());
    }

    @Test
    void appConfig_02() {
        final var config = new AnnotationConfigApplicationContext(AppConfig02.class).getBean(EnableByJmsConfig.class);

        Assertions.assertEquals(1, config.scan().size());
        Assertions.assertEquals(Case01.class, config.scan().get(0));
        Assertions.assertEquals(null, config.ttl());
        Assertions.assertEquals(0, config.dispatchFns().size());
    }

    @Test
    void scan_01() {
        new AnnotationConfigApplicationContext(AppConfig01.class).getBean(Case01.class);
    }

    @Test
    void scan_02() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig02.class);

        appCtx.getBean(Case01.class);

        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> appCtx.getBean(Case02.class));
    }

    @Test
    void scan_03() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.class);

        appCtx.getBean(Case01.class);

        appCtx.getBean(Case02.class);
    }

    @Test
    void name_01() {
        new AnnotationConfigApplicationContext(AppConfig01.class).getBean(Case01.class.getSimpleName(), Case01.class);
    }

    @Test
    void name_02() {
        new AnnotationConfigApplicationContext(AppConfig.class).getBean(Case02.NAME, Case02.class);
    }
}
