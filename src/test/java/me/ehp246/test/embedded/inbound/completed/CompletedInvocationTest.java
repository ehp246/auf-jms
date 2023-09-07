package me.ehp246.test.embedded.inbound.completed;

import java.util.concurrent.ExecutionException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufjms.api.inbound.InboundEndpoint;

/**
 * @author Lei Yang
 *
 */
@Timeout(2)
@SpringBootTest(classes = { AppConfig.class, CompletedListener.class }, properties = {
        "comp1.name=completedListener" }, webEnvironment = WebEnvironment.NONE)
class CompletedInvocationTest {
    @Autowired
    private SendQ1 q1;
    @Autowired
    private CompletedListener listener;

    @Test
    void completed_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();
        q1.send(id);

        final var completed = listener.takeCompleted();

        Assertions.assertEquals(id, completed.bound().msg().correlationId());
        Assertions.assertEquals(true, completed.returned() instanceof String);
        Assertions.assertEquals(id, completed.returned().toString());
    }

    @Test
    void bound_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();
        q1.send(id);

        final var bound = listener.takeBound();

        Assertions.assertEquals(true, bound.invocable().instance() instanceof OnMsg);
        Assertions.assertEquals(id, bound.arguments()[1]);
        Assertions.assertEquals("Send", bound.arguments()[0]);
    }

    @Test
    void completed_02() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.class);

        Assertions.assertEquals(null,
                appCtx.getBean("inboundEndpoint-0", InboundEndpoint.class).invocationListener(),
                "should have no bean without a name");

        appCtx.close();
    }
}
