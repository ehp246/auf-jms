package me.ehp246.test.embedded.dispatch.listener;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.jms.JMSException;
import me.ehp246.aufjms.api.dispatch.BodyPublisher;
import me.ehp246.test.embedded.dispatch.listener.AppConfig.BodyCase01;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class })
class ListenerTest {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private BodyCase01 case01;

    @Test
    void body_01() throws InterruptedException, ExecutionException {
        case01.ping();

        Assertions.assertEquals(null, appConfig.onDispatchRef.get().dispatch().body());
        Assertions.assertEquals(null, appConfig.preRef.get().dispatch().body());
        Assertions.assertEquals(appConfig.postRef.get().dispatch().body(),
                appConfig.preRef.get().dispatch().body());
    }

    @Test
    void body_02() throws InterruptedException, ExecutionException {
        final var expected = Map.of("1", "2");
        case01.ping(expected);

        Assertions.assertEquals(expected, appConfig.onDispatchRef.get().dispatch().body());
        Assertions.assertEquals(expected, appConfig.preRef.get().dispatch().body());
    }

    @Test
    void body_03() throws InterruptedException, ExecutionException {
        final BodyPublisher expected = "1"::toString;

        case01.ping(expected);

        Assertions.assertEquals(expected, appConfig.onDispatchRef.get().dispatch().body());
        Assertions.assertEquals(expected, appConfig.preRef.get().dispatch().body());
    }

    @Test
    void body_04() throws InterruptedException, ExecutionException, JMSException {
        final BodyPublisher expected = "1"::toString;

        case01.ping(expected);

        Assertions.assertEquals(expected, appConfig.onDispatchRef.get().dispatch().body());
        Assertions.assertEquals(expected, appConfig.preRef.get().dispatch().body());
    }
}
