package me.ehp246.test.embedded.dispatch.type;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.jms.JMSException;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;
import me.ehp246.test.embedded.dispatch.type.AppConfig.OfTypeCase01;

/**
 * @author Lei Yang
 *
 */
@Timeout(5)
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, EmbeddedArtemisConfig.class })
class TypeTest {
    @Autowired
    private TestQueueListener listener;

    @Autowired
    private OfTypeCase01 case01;

    @BeforeEach
    void reset() {
        listener.reset();
    }

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

        Assertions.assertEquals(type, listener.takeReceived().getJMSType());
    }

    @Test
    void type_03() throws JMSException {
        case01.ping(null);

        Assertions.assertEquals(null, listener.takeReceived().getJMSType());
    }

    @Test
    void type_04() throws JMSException {
        case01.ping("");

        Assertions.assertEquals("", listener.takeReceived().getJMSType());
    }
}
