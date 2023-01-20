package me.ehp246.test.embedded.enablebyjms.case03;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;

/**
 * @author Lei Yang
 *
 */
class TtlTest {
    @Test
    void ttl_01() {
        final var appCtx = new AnnotationConfigApplicationContext();
        
        appCtx.setEnvironment(new MockEnvironment().withProperty("ttl", "PT1S"));
        appCtx.register(AppConfigs.Config01.class);
        appCtx.refresh();

        Assertions.assertEquals(Duration.parse("PT1S"), appCtx.getBean(EnableByJmsConfig.class).ttl());

        appCtx.close();
    }

    @Test
    void ttl_02() {
        final var appCtx = new AnnotationConfigApplicationContext();

        appCtx.setEnvironment(new MockEnvironment().withProperty("ttl", "PT1S"));
        appCtx.register(AppConfigs.Config02.class);
        appCtx.refresh();

        Assertions.assertEquals(null, appCtx.getBean(EnableByJmsConfig.class).ttl());

        appCtx.close();
    }

    @Test
    void ttl_03() {
        final var appCtx = new AnnotationConfigApplicationContext();

        appCtx.setEnvironment(new MockEnvironment().withProperty("ttl", "PT12S"));
        appCtx.register(AppConfigs.Config03.class);
        appCtx.refresh();

        Assertions.assertEquals(Duration.parse("PT12S"), appCtx.getBean(EnableByJmsConfig.class).ttl());

        appCtx.close();
    }

    @Test
    void ttl_04() {
        Assertions.assertThrows(UnsatisfiedDependencyException.class,
                () -> new AnnotationConfigApplicationContext(AppConfigs.Config03.class));
    }
}
