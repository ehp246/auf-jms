package me.ehp246.test.embedded.request.case03;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.exception.JmsDispatchException;

/**
 * Both Global and Local timeout.
 *
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "me.ehp246.aufjms.request.timeout=PT10S" })
class RequestTest {
    @Autowired
    private ClientProxy proxy;

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void timeout_01() {
        final var cause = Assertions.assertThrows(JmsDispatchException.class, () -> proxy.inc(0)).getCause();

        Assertions.assertEquals(TimeoutException.class, cause.getClass(), "should use local timeout");
    }
}
