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
        IntStream.range(0, 100).forEach(toInbox::ping);
    }

    @Test
    void send_003() throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            toInbox.ping(i);
            // Wait for n minutes.
            Thread.sleep(1 * 60000);
        }
    }
}
