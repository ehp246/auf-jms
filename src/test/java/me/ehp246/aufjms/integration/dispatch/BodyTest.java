package me.ehp246.aufjms.integration.dispatch;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.integration.dispatch.AppConfig.BodyCase01;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@Timeout(5)
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, EmbeddedArtemisConfig.class })
class BodyTest {
    @Autowired
    private ToJson toJson;
    @Autowired
    private TestQueueListener listener;
    @Autowired
    private BodyCase01 case01;

    @Test
    void destination_01() {
        case01.ping();
        
        final var received = listener.takeReceived();
        
        Assertions.assertEquals(true, received != null);
        Assertions.assertEquals(true, received instanceof TextMessage);
    }

    @Test
    void correlId_01() throws JMSException {
        case01.ping();

        final var received = listener.takeReceived();

        Assertions.assertEquals(true, received.getJMSCorrelationID() != null);
    }

    @Test
    void replyTo_01() throws JMSException {
        case01.ping();

        final var received = listener.takeReceived();

        Assertions.assertEquals(null, received.getJMSReplyTo());
    }

    @Test
    void body_01() throws JMSException {
        case01.ping();

        final var received = (TextMessage) listener.takeReceived();

        Assertions.assertEquals(null, received.getText());
    }

    @Test
    void body_02() throws JMSException {
        final var now = Instant.now();
        case01.ping(Map.of("now", now));

        final var received = (TextMessage) listener.takeReceived();

        Assertions.assertEquals(true, received.getText().contains(now.toString()));
    }

    @Test
    void body_03() throws JMSException {
        final var now = Instant.now();
        case01.ping(Map.of("now", now), -1);

        final var received = (TextMessage) listener.takeReceived();

        final var text = received.getText();
        Assertions.assertEquals(true, text.contains(now.toString()));
        Assertions.assertEquals(true, text.contains(toJson.apply(List.of(-1))));
    }
}
