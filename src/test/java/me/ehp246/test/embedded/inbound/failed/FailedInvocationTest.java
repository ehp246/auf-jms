package me.ehp246.test.embedded.inbound.failed;

import java.util.concurrent.ExecutionException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.test.embedded.inbound.failed.dltopic.OnDlqMsg;
import me.ehp246.test.embedded.inbound.failed.invocation.FailMsg;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {}, webEnvironment = WebEnvironment.NONE)
class FailedInvocationTest {
    @Autowired
    private SendQ1 sendQ1;

    @Autowired
    private SendQ2 sendQ2;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private FailMsg onMsg;

    @Autowired
    private OnDlqMsg onDlqMsg;

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();

        sendQ1.failedMsg(id);

        final var failed = appConfig.conRef1.get();

        Assertions.assertEquals(onMsg.ex, failed.thrown());
        Assertions.assertEquals(id, failed.bound().msg().correlationId());
        Assertions.assertEquals(1, failed.bound().msg().deliveryCount());
    }

    @Test
    void dltopic_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();

        sendQ2.failedMsg(id);

        Assertions.assertEquals(id, onDlqMsg.msgRef.get().correlationId());
    }
}
