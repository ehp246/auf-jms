package me.ehp246.broker.sb;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import javax.jms.JMSException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnabledIfSystemProperty(named = "me.ehp246.broker.sb", matches = "true")
@TestInstance(Lifecycle.PER_CLASS)
class SbTest {
    @Autowired
    private ToInbox toInbox;

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

}
