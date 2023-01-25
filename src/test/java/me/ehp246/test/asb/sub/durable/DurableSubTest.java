package me.ehp246.test.asb.sub.durable;

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
@EnabledIfSystemProperty(named = "me.ehp246.aufjms", matches = "true")
@ActiveProfiles("local")
class DurableSubTest {
    @Autowired
    private OnMsg onMsg;

    @Test
    void take_01() throws InterruptedException, ExecutionException {
        Thread.sleep(10000000);
    }
}
