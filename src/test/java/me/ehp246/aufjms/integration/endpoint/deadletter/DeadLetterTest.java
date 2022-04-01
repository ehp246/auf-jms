package me.ehp246.aufjms.integration.endpoint.deadletter;

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
@SpringBootTest(classes = { AppConfig.class }, properties = {}, webEnvironment = WebEnvironment.NONE)
class DeadLetterTest {
    @Autowired
    private SendRef1 sendRef1;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private OnMsg onMsg;

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();

        sendRef1.send(id);

        final var dead = appConfig.ref1.get();

        Assertions.assertEquals(onMsg.ex, dead.ex());
        Assertions.assertEquals(id, dead.msg().correlationId());
    }
}
