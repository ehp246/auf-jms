package me.ehp246.test.asb;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import jakarta.jms.JMSException;
import me.ehp246.test.asb.dlq.dlq.LetterCollection;
import me.ehp246.test.asb.replyto.reply.OnEchoInstant;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "me.ehp246.aufjms", matches = "true")
class SbTest {
    @Autowired
    private Echo echo;

    @Autowired
    private OnEchoInstant onReply;

    @Autowired
    private ToInbox toInbox;

    @Autowired
    private ToDlq toDlq;

    @Autowired
    private LetterCollection letters;

    @Test
    void send_001() {
        toInbox.ping();
    }

    @Test
    void send_002() {
        IntStream.range(0, 10).forEach(toInbox::ping);
    }

    @Disabled
    @Test
    void send_003() throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            toInbox.ping(i);
            // Wait for n minutes.
            Thread.sleep(120 * 60000);
        }
    }

    @Test
    void send_004() throws JMSException, InterruptedException {
        final var count = 10000;
        final var exe = new ExecutorCompletionService<>(Executors.newCachedThreadPool());
        for (int i = 0; i <= count; i++) {
            exe.submit(() -> {
                toInbox.ping();
                return null;
            });
        }

        for (int i = 0; i <= count; i++) {
            exe.take();
        }
    }

    @Test
    void send_005() throws JMSException, InterruptedException, ExecutionException {
        final var run = (Runnable) () -> {
            for (int i = 0; i <= 1000000; i++) {
                toInbox.ping(i++);
            }
        };

        CompletableFuture.allOf(CompletableFuture.runAsync(run), CompletableFuture.runAsync(run),
                CompletableFuture.runAsync(run), CompletableFuture.runAsync(run), CompletableFuture.runAsync(run))
                .get();
    }

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

    @Disabled
    @Test
    void dql_01() throws InterruptedException, ExecutionException {
        final var instant = Instant.now();

        toDlq.throwIt(instant);
//        toDlq.throwIt(instant);
//        toDlq.throwIt(instant);

        Assertions.assertEquals(true, instant.equals(letters.take()));
    }
}
