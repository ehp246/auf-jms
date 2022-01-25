package me.ehp246.broker.sb;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import javax.jms.JMSException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.broker.sb.replyto.reply.OnReplyEchoInstant;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "me.ehp246.broker.sb", matches = "true")
class SbTest {
    @Autowired
    private Echo echo;

    @Autowired
    private OnReplyEchoInstant onReply;

    @Autowired
    private ToInbox toInbox;

    @Autowired
    private ToDlq toDlq;

    @Test
    void send_001() {
        toInbox.ping();
    }

    @Test
    void send_002() {
        IntStream.range(0, 100).forEach(toInbox::ping);
    }

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

    @Test
    void dql_01() throws InterruptedException {
        toDlq.throwIt();
        Thread.sleep(1000);
    }
}
