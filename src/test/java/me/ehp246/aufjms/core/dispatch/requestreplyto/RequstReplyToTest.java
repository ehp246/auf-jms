package me.ehp246.aufjms.core.dispatch.requestreplyto;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.core.dispatch.requestreplyto.case01.AppConfigCase01;
import me.ehp246.aufjms.core.dispatch.requestreplyto.case02.AppConfigCase02;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class RequstReplyToTest {
    @Test
    void property_01() {
        Assertions.assertThrows(BeanCreationException.class,
                () -> new AnnotationConfigApplicationContext(AppConfigCase01.class));
    }

    @Test
    void property_02() {
        final var replyTopic = UUID.randomUUID().toString();
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("replyTo", replyTopic));
        appCtx.register(AppConfigCase01.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(AppConfigCase01.Case01.class) != null);

        Assertions.assertEquals(replyTopic, appCtx.getBean(EnableByJmsConfig.class).requestReplyAt().name());
        Assertions.assertEquals(true, appCtx.getBean(EnableByJmsConfig.class).requestReplyAt() instanceof AtQueue);
    }

    @Test
    void property_03() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(AppConfigCase02.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(AppConfigCase02.Case01.class) != null);

        Assertions.assertEquals("b82fc4f2-66e6-420b-b9c3-dc960638c24b",
                appCtx.getBean(EnableByJmsConfig.class).requestReplyAt().name());
        Assertions.assertEquals(true, appCtx.getBean(EnableByJmsConfig.class).requestReplyAt() instanceof AtTopic);
    }
}
