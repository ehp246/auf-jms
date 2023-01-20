package me.ehp246.test.embedded.reply;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.test.embedded.reply.reply.OnReplyEchoInstant;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "me.ehp246.broker.sb", matches = "true")
class ReplyTest {
    @Autowired
    private Echo echo;
    @Autowired
    private OnReplyEchoInstant onReply;

    @Test
    void reply_01() throws InterruptedException, ExecutionException {
        final var now = Instant.now();

        echo.echoInstant(now);

        Assertions.assertEquals(true, onReply.take().equals(now));
    }

    @Test
    void reply_02() throws InterruptedException, ExecutionException {
        echo.echoInstant(null);

        Assertions.assertEquals(null, onReply.take());
    }
}
