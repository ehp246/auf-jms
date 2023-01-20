package me.ehp246.test.asb.eventgrid.storage;

import java.util.concurrent.ExecutionException;

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
@EnabledIfSystemProperty(named = "me.ehp246.broker.sb", matches = "true")
@ActiveProfiles("local")
class EventLoggerTest {
    @Autowired
    private EventLogger event;

    @Test
    void take_01() throws InterruptedException, ExecutionException {
        event.take();
    }
}
