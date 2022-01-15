package me.ehp246.broker.sb;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
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
class SbTest {
    @Autowired
    private ToInbox toInbox;

    @Test
    void send_001() {
        toInbox.ping();
    }

    @Test
    void send_002() {
        IntStream.range(0, 50).forEach(toInbox::ping);
    }

    @Test
    void send_003() throws InterruptedException {
        toInbox.ping();
        // Wait for 11 minutes.
        Thread.sleep(11 * 60000);
        toInbox.ping();
    }
}
