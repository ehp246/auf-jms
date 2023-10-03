package me.ehp246.test.embedded.log4jcontext;

import java.util.concurrent.ExecutionException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.test.embedded.log4jcontext.Log4jContextCase.Order;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, OnPing.class, ThreadContextDispatchListener.class,
        OnPing2.class, ThreadContextInvocationLIstener.class }, properties = {
        "me.ehp246.aufjms.dispatchlogger.enabled=true" }, webEnvironment = WebEnvironment.NONE)
class Log4jContextTest {
    @Autowired
    private Log4jContextCase case1;
    @Autowired
    private OnPing onPing;
    @Autowired
    private OnPing2 onPing2;
    @Autowired
    private ThreadContextDispatchListener dispatchListener;
    @Autowired
    private ThreadContextInvocationLIstener invocationListener;

    @Test
    void logging_01() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();

        case1.ping(expected);

        Assertions.assertEquals(expected, dispatchListener.take());

        Assertions.assertEquals(expected, onPing.take());
    }

    @Test
    void invocable_01() throws InterruptedException, ExecutionException {
        case1.ping2(1234, new Order(4321, 2));

        final var context = onPing2.take();

        Assertions.assertEquals("1234", context.get("accountId"));
        Assertions.assertEquals("4321", context.get("order.OrderId"));
        Assertions.assertEquals("2", context.get("order.amount"));
    }

    @Test
    void invocationListener_01() throws InterruptedException, ExecutionException {
        case1.ping2(1234, new Order(4321, 2));

        final var context = invocationListener.take();

        Assertions.assertEquals("1234", context.get("accountId"));
        Assertions.assertEquals("4321", context.get("order.OrderId"));
        Assertions.assertEquals("2", context.get("order.amount"));
    }
}
