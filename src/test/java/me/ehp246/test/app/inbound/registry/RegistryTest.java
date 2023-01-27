package me.ehp246.test.app.inbound.registry;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.InvocableTypeDefinition;
import me.ehp246.aufjms.core.reflection.ReflectedType;
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

    @Test
    void test_03() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.Config01.class);

        final var inbound = appCtx.getBean("inboundEndpoint-0", InboundEndpoint.class);

        final var typeRegistry = inbound.typeRegistry();

        Assertions.assertEquals(0, typeRegistry.registered().size());

        final var method = ReflectedType.reflect(String.class).findMethod("toString");

        typeRegistry.register(
                new InvocableTypeDefinition(Set.of("OnMsg"), String.class, Map.of("", method, "toString", method)));

        Assertions.assertEquals(1, typeRegistry.registered().size());

        final var onMsg = typeRegistry.registered().get("OnMsg");

        Assertions.assertEquals(String.class, onMsg.type());
        Assertions.assertEquals(method, onMsg.methods().get(""));
        Assertions.assertEquals(method, onMsg.methods().get("toString"));

        appCtx.close();
    }
}
