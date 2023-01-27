package me.ehp246.test.embedded.dispatch.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.jms.JMSException;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;
import me.ehp246.test.embedded.dispatch.type.AppConfig.OfCorrelationIdCase01;

/**
 * @author Lei Yang
 *
 */
@Timeout(5)
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, EmbeddedArtemisConfig.class })
class CorrelationIdTest {
    @Autowired
    private TestQueueListener listener;

    @Autowired
    private OfCorrelationIdCase01 case01;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void correlationId_01() throws JMSException {
        case01.ping();

        Assertions.assertEquals(36, listener.take().getJMSCorrelationID().length());
    }

    @Test
    void correlationId_02() throws JMSException {
        case01.ping("");

        Assertions.assertEquals("", listener.take().getJMSCorrelationID());
    }

    @Test
    void correlationId_03() throws JMSException {
        case01.ping(null);

        Assertions.assertEquals(null, listener.take().getJMSCorrelationID());
    }
}
