package me.ehp246.aufjms.azure.dispatch;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Lei Yang
 *
 */
@ActiveProfiles("yangle")
@SpringBootTest(classes = { AppConfig.class })
@EnabledIfSystemProperty(named = "me.ehp246.aufjms.azure", matches = "true")
class DispatchTest {
    @Autowired
    private AppConfig.Case01 case01;

    @Test
    void dispatch_01() {
        case01.ping();

        case01.ping(Map.of("now", Instant.now()), 1);
    }
}
