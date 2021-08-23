package me.ehp246.aufjms.integration.dispatch;

import javax.jms.JMSException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.integration.dispatch.AppConfig.OfTypeCase01;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@Timeout(5)
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, EmbeddedArtemisConfig.class })
class JmsPropertyTest {
    @Autowired
    private TestQueueListener listener;
    @Autowired
    private OfTypeCase01 case01;

    @Test
    void type_01() throws JMSException {
        case01.ping();

        final var received = listener.takeReceived();

        Assertions.assertEquals("Ping", received.getJMSType());
    }

    @Test
    void type_02() throws JMSException {
        final var type = UUID.randomUUID().toString();
        case01.ping(type);

        final var received = listener.takeReceived();

        Assertions.assertEquals(type, received.getJMSType());
    }

    @Test
    void type_03() throws JMSException {
        case01.ping(null);

        Assertions.assertEquals("default", listener.takeReceived().getJMSType(),
                "should use the annotated for the default");
    }

    @Test
    void type_04() throws JMSException {
        case01.ping("");

        Assertions.assertEquals("", listener.takeReceived().getJMSType());
    }
}
