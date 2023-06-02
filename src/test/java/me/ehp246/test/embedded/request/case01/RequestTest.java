package me.ehp246.test.embedded.request.case01;

import java.util.concurrent.TimeoutException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.exception.JmsDispatchException;

/**
 * Local timeout and replyAt placeholder.
 *
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "reply.timeout=PT1S", "reply.topic=reply.topic" })
class RequestTest {
    @Autowired
    private ClientProxy proxy;
    @Autowired
    private ClientTimeoutProxy timeoutProxy;

    @Test
    void test_01() {
        Assertions.assertEquals(0, proxy.inc(-1));
    }

    @Test
    void test_02() {
        final var person = new Person(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var swaped = proxy.swapName(person);

        Assertions.assertEquals(person.firstName(), swaped.lastName());
        Assertions.assertEquals(person.lastName(), swaped.firstName());
    }

    @Test
    void timeout_01() {
        Assertions.assertThrows(TimeoutException.class, () -> timeoutProxy.incThrowing(0));
    }

    @Test
    void timeout_02() {
        final var cause = Assertions.assertThrows(JmsDispatchException.class, () -> timeoutProxy.nonInc(0)).getCause();

        Assertions.assertEquals(TimeoutException.class, cause.getClass());
    }
}
