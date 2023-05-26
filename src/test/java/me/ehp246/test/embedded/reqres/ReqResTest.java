package me.ehp246.test.embedded.reqres;

import java.util.concurrent.TimeoutException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "reply.timeout=PT0S" })
class ReqResTest {
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
}
