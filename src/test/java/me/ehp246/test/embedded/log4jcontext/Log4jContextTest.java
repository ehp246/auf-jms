package me.ehp246.test.embedded.log4jcontext;

import java.util.concurrent.ExecutionException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, OnPing.class, ThreadContextDispatchListener.class }, properties = {
        "me.ehp246.aufjms.dispatchlogger.enabled=true" }, webEnvironment = WebEnvironment.NONE)
class Log4jContextTest {
    @Autowired
    private Log4jContextCase case1;
    @Autowired
    private OnPing onPing;
    @Autowired
    private ThreadContextDispatchListener dispatchListener;

    @Test
    void logging_01() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();

        case1.ping(expected);

        Assertions.assertEquals(expected, dispatchListener.take());

        Assertions.assertEquals(expected, onPing.take());
    }
}
