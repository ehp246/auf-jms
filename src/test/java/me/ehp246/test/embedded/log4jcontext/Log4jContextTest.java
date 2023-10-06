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
@SpringBootTest(classes = { AppConfig.class }, properties = {
        "me.ehp246.aufjms.dispatchlogger.enabled=true" }, webEnvironment = WebEnvironment.NONE)
class Log4jContextTest {
    @Autowired
    private Log4jContextCase case1;
    @Autowired
    private OnPing onPing;
    @Autowired
    private OnPing2 onPing2;
    @Autowired
    private OnPingOnBody onPingOnBody;
    @Autowired
    private ThreadContextDispatchListener dispatchListener;
    @Autowired
    private Log4jContextInvocationLIstener invocationListener;

    @Test
    void property_01() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();

        case1.ping(expected);

        Assertions.assertEquals(expected, dispatchListener.take().get("OrderId"));
        Assertions.assertEquals(expected, onPing.take().get("OrderId"));
    }

    @Test
    void dispatch_01() throws InterruptedException, ExecutionException {
        case1.ping2(1234, new Order(4321, 2));

        final var localContext = dispatchListener.take();

        Assertions.assertEquals("1234", localContext.get("accountId"));
        Assertions.assertEquals("4321", localContext.get("order.id"));
        Assertions.assertEquals("2", localContext.get("order.amount"));
    }

    @Test
    void invocable_01() throws InterruptedException, ExecutionException {
        case1.ping2(1234, new Order(4321, 2));

        final var remoteContext = onPing2.take();

        Assertions.assertEquals("1234", remoteContext.get("accountId"));
    }

    @Test
    void bodyIntro_01() throws InterruptedException, ExecutionException {
        final var order = new Order((int) (Math.random() * 100), (int) (Math.random() * 100));
        case1.pingOnBody(order);

        final var remoteContext = onPingOnBody.take();

        Assertions.assertEquals(order.id() + "", remoteContext.get("Order_OrderId"));
        Assertions.assertEquals(order.amount() + "", remoteContext.get("Order_amount"));
    }

    @Test
    void invocationListener_01() throws InterruptedException, ExecutionException {
        final var accountId = (int) (Math.random() * 100);
        case1.ping2(accountId, new Order((int) (Math.random() * 100), (int) (Math.random() * 100)));

        final var context = invocationListener.take();

        Assertions.assertEquals(accountId + "", context.get("accountId"));
    }
}
