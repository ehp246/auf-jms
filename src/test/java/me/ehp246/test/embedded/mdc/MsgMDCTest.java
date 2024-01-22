package me.ehp246.test.embedded.mdc;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.ThreadContext;
import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.test.embedded.mdc.MsgMDCCase.Name;
import me.ehp246.test.embedded.mdc.MsgMDCCase.Order;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {
        "me.ehp246.aufjms.dispatchlogger.enabled=true" }, webEnvironment = WebEnvironment.NONE)
class MsgMDCTest {
    @Autowired
    private MsgMDCCase case1;
    @Autowired
    private OnPing onPing;
    @Autowired
    private OnPing2 onPing2;
    @Autowired
    private OnPingOnBody onPingOnBody;
    @Autowired
    private MsgMDCDispatchListener dispatchListener;
    @Autowired
    private MsgMDCInvocationLIstener invocationListener;

    @BeforeEach
    void clear() {
        ThreadContext.clearAll();
        onPing.reset();
    }

    @Test
    void msgMDCProperty_01() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();

        case1.ping(expected);

        Assertions.assertEquals(expected, dispatchListener.take().get("OrderId"));
        Assertions.assertEquals(expected, onPing.take().get("OrderId"));
    }

    @Test
    void dispatch_01() throws InterruptedException, ExecutionException {
        case1.ping2(1234, new Order(4321, 2));

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(4, localContext.size());
        Assertions.assertEquals("1234", localContext.get("accountId"));
    }

    @Test
    void dispatch_02() throws InterruptedException, ExecutionException {
        final var name = new MsgMDCCase.Name(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        case1.ping(name);

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(4, localContext.size());
        Assertions.assertEquals(name.toString(), localContext.get("name"));
    }

    @Test
    void dispatch_02_01() throws InterruptedException, ExecutionException {
        case1.ping((Name) null);

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(4, localContext.size());
        Assertions.assertEquals(null, localContext.get("name"));
    }

    @Test
    void dispatch_03() throws InterruptedException, ExecutionException {
        final var name = new MsgMDCCase.Name(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        case1.pingWithName(name);

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(4, localContext.size());
        Assertions.assertEquals(name.toString(), localContext.get("WithName."));
    }

    @Test
    void dispatch_04() throws InterruptedException, ExecutionException {
        final var name = new MsgMDCCase.Name(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        case1.pingIntroWithName(name);

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(5, localContext.size());
        Assertions.assertEquals(name.firstName(), localContext.get("WithName.firstName"));
        Assertions.assertEquals(name.lastName(), localContext.get("WithName.lastName"));
    }

    @Test
    void dispatch_04_01() throws InterruptedException, ExecutionException {
        case1.pingIntroWithName(null);

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(5, localContext.size());
        Assertions.assertEquals(null, localContext.get("WithName.firstName"));
        Assertions.assertEquals(null, localContext.get("WithName.lastName"));
    }

    @Test
    void dispatch_05() throws InterruptedException, ExecutionException {
        final var name = new MsgMDCCase.Name(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        case1.pingIntro(name);

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(5, localContext.size());
        Assertions.assertEquals(name.firstName(), localContext.get("firstName"));
        Assertions.assertEquals(name.lastName(), localContext.get("lastName"));
    }

    @Test
    void dispatch_06() throws InterruptedException, ExecutionException {
        final var name = new MsgMDCCase.Name(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        case1.ping(name, UUID.randomUUID().toString());

        final var localContext = dispatchListener.take();

        Assertions.assertEquals(name.toString(), localContext.get("name"), "should follow the body");
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
