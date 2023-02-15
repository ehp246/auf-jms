package me.ehp246.test.asb.queue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "me.ehp246.aufjms", matches = "true")
@ActiveProfiles("local")
class QueueTest {
    @Autowired
    private ToInbox toInbox;

    @Test
    void run_01() throws InterruptedException {
        Thread.sleep(120 * 60000);
    }

    @Test
    void send_01() {
        toInbox.ping();
    }
}
