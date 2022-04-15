package me.ehp246.broker.sb.sub;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import me.ehp246.broker.sb.sub.localevent.LocalEvent;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "me.ehp246.broker.sb", matches = "true")
@ActiveProfiles("local")
class SubTest {
    @Autowired
    private OnEcho onCount;
    @Autowired
    private ToEchoTopic echoTopic;

    @Autowired
    private LocalEvent event;

    @Test
    void sub_01() throws InterruptedException, ExecutionException {
        echoTopic.echo(1);
        echoTopic.echo(2);
        onCount.get();
    }

    @Test
    void localEvent_01() throws InterruptedException, ExecutionException {
        event.take();
    }
}
