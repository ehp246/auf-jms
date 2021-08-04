package me.ehp246.aufjms.integration.enablebyjms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufjms.integration.enablebyjms.case01.AppConfig01;
import me.ehp246.aufjms.integration.enablebyjms.case01.Case01;
import me.ehp246.aufjms.integration.enablebyjms.case02.AppConfig02;
import me.ehp246.aufjms.integration.enablebyjms.case02.Case02;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class EnableByJmsTest {

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
}
