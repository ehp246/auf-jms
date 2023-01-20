package me.ehp246.test.asb;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import me.ehp246.test.asb.replyto.reply.OnEchoInstant;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "me.ehp246.broker.sb", matches = "true")
@ActiveProfiles("local")
class ReplyTest {
    @Autowired
    private Echo echo;
    @Autowired
    private OnEchoInstant onReply;

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
