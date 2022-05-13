package me.ehp246.aufjms.core.byjms.delay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.core.byjms.delay.AppConfig.DelayConfig02;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class DelayTest {
    @Test
    void delay_01() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.DelayConfig01.class);

        Assertions.assertEquals("PT0.112S",
                appCtx.getBean(EnableByJmsConfig.class).delay().toString());
    }

    @Test
    void delay_02() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(AppConfig.DelayConfig01.class);
        appCtx.setEnvironment(new MockEnvironment().withProperty("delay", "PT1S"));
        appCtx.refresh();

        Assertions.assertEquals("PT1S", appCtx.getBean(EnableByJmsConfig.class).delay().toString());
    }

    @Test
    void delay_03() {
        final var appCtx = new AnnotationConfigApplicationContext(DelayConfig02.class);

        Assertions.assertEquals(null, appCtx.getBean(EnableByJmsConfig.class).delay());
    }
}
