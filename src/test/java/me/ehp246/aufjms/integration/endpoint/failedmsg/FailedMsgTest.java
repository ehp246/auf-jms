package me.ehp246.aufjms.integration.endpoint.failedmsg;

import java.util.concurrent.ExecutionException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufjms.integration.endpoint.failedmsg.dltopic.OnDlTopicMsg;
import me.ehp246.aufjms.integration.endpoint.failedmsg.failed.FailMsg;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {}, webEnvironment = WebEnvironment.NONE)
class FailedMsgTest {
    @Autowired
    private SendQ1 sendQ1;

    @Autowired
    private SendQ2 sendQ2;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private FailMsg onMsg;

    @Autowired
    private OnDlTopicMsg onDlqMsg;

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();

        sendQ1.send(id);

        final var dead = appConfig.conRef1.get();

        Assertions.assertEquals(onMsg.ex, dead.exception());
        Assertions.assertEquals(id, dead.msg().correlationId());
    }

    void dltopic_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();

        sendQ2.send(id);

        Assertions.assertEquals(id, onDlqMsg.msgRef.get().correlationId());
    }
}
