package me.ehp246.aufjms.integration.enablebyjms.propertycase.to;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufjms.integration.enablebyjms.propertycase.to.AppConfig.Case01;

/**
 * @author Lei Yang
 *
 */
class ToTest {
    private AnnotationConfigApplicationContext appCtx;

    @BeforeEach
    void create() {
        appCtx = new AnnotationConfigApplicationContext();
    }

    @AfterEach
    void close() {
        if (appCtx != null) {
            appCtx.close();
        }
    }

    @Test
    void property_01() {
        Assertions.assertThrows(BeanCreationException.class,
                () -> new AnnotationConfigApplicationContext(AppConfig.class));
    }

    @Test
    void property_02() {
        appCtx.setEnvironment(new MockEnvironment().withProperty("holder", "queue"));
        appCtx.register(AppConfig.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(Case01.class) != null);
    }

    @Test
    void property_03() {
        appCtx.setEnvironment(new MockEnvironment().withProperty("holder", ""));
        appCtx.register(AppConfig.class);

        Assertions.assertThrows(BeanCreationException.class, appCtx::refresh);
    }
}
