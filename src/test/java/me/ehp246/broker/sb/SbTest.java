package me.ehp246.broker.sb;

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
}
