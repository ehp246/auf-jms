package me.ehp246.aufjms.integration.endpoint.topic.sub;

import java.util.concurrent.ExecutionException;

import javax.jms.Topic;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class })
public class TopicTest {
    @Autowired
    private OnMsg onMsg;
    @Autowired
    private ToTopic toTopic;

    @Test
    @Timeout(2)
    void test_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();

        toTopic.msg(id);

        final var msg = onMsg.take();

        Assertions.assertEquals(id, msg.correlationId());
        Assertions.assertEquals(true, msg.destination() instanceof Topic);
    }
}
