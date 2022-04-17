package me.ehp246.aufjms.integration.endpoint.completed;

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
class CompletedInvocationTest {
    @Autowired
    private SendQ1 q1;

    @Test
    void completed_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();
        q1.send(id);
        
        final var completed = AppConfig.comp1Ref.get();

        Assertions.assertEquals(id, completed.msg().correlationId());
        Assertions.assertEquals(true, completed.returned() instanceof String);
        Assertions.assertEquals(id, completed.returned().toString());
    }

}
