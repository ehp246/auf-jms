package me.ehp246.aufjms.core.dispatch.delay;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufjms.core.dispatch.delay.AppConfig.Case01;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class DelayTest {
    @Test
    void property_01() {
        Assertions.assertThrows(BeanCreationException.class,
                () -> new AnnotationConfigApplicationContext(AppConfig.class));
    }

    @Test
    void property_02() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("holder", "PT1S"));
        appCtx.register(AppConfig.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(Case01.class) != null);
    }

    @Test
    void property_03() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("holder", UUID.randomUUID().toString()));
        appCtx.register(AppConfig.class);

        Assertions.assertThrows(BeanCreationException.class, appCtx::refresh);
    }
}
