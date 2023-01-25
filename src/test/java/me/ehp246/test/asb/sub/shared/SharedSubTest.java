package me.ehp246.test.asb.sub.shared;

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
class SharedSubTest {
    @Autowired
    private ToEchoTopic echoTopic;
    @Autowired
    private OnMsg onMsg;

    @Test
    void sub_01() throws InterruptedException, ExecutionException {
        echoTopic.echo(1);
        onMsg.take();
    }

    @Test
    void send_01() throws InterruptedException, ExecutionException {
        echoTopic.echo(1);
    }

    @Test
    void take_01() throws InterruptedException, ExecutionException {
        Thread.sleep(1000000);
    }
}
