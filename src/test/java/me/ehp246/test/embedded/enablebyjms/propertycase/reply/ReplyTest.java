package me.ehp246.test.embedded.enablebyjms.propertycase.reply;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.test.embedded.enablebyjms.propertycase.reply.AppConfig.Case01;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class ReplyTest {
    @Test
    void property_01() {
        Assertions.assertThrows(BeanCreationException.class,
                () -> new AnnotationConfigApplicationContext(AppConfig.class));
    }

    @Test
    void property_02() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("holder", "queue"));
        appCtx.register(AppConfig.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(Case01.class) != null);
    }

    @Test
    void property_03() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("holder", ""));
        appCtx.register(AppConfig.class);

        Assertions.assertDoesNotThrow(appCtx::refresh, "should allow empty replyTo");
    }
}
