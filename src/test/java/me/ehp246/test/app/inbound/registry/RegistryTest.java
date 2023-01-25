package me.ehp246.test.app.inbound.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.test.mock.action.OnMsg;

/**
 * @author Lei Yang
 *
 */
class RegistryTest {

    @Test
    void test_01() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.Config01.class);

        final var inbound = appCtx.getBean("inboundEndpoint-0", InboundEndpoint.class);

        final var typeRegistry = inbound.typeRegistry();

        Assertions.assertEquals(0, typeRegistry.registered().size());

        Assertions.assertThrows(UnsupportedOperationException.class, typeRegistry.registered()::clear);

        appCtx.close();
    }

    @Test
    void test_02() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.Config02.class);

        final var registered = appCtx.getBean("inboundEndpoint-0", InboundEndpoint.class).typeRegistry().registered();

        Assertions.assertEquals(1, registered.size());
        Assertions.assertEquals(OnMsg.class, registered.get("OnMsg").type());

        Assertions.assertEquals(0,
                appCtx.getBean("inboundEndpoint-1", InboundEndpoint.class).typeRegistry().registered().size());

        appCtx.close();
    }
}
