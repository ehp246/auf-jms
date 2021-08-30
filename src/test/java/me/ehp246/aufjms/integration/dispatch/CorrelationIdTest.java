package me.ehp246.aufjms.integration.dispatch;

import javax.jms.JMSException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.integration.dispatch.AppConfig.OfCorrelationIdCase01;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

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

    @Test
    void correlationId_01() throws JMSException {
        case01.ping();

        Assertions.assertEquals(36, listener.takeReceived().getJMSCorrelationID().length());
    }

    @Test
    void correlationId_02() throws JMSException {
        case01.ping("");

        Assertions.assertEquals("", listener.takeReceived().getJMSCorrelationID());
    }

    @Test
    void correlationId_03() throws JMSException {
        case01.ping(null);

        Assertions.assertEquals(null, listener.takeReceived().getJMSCorrelationID());
    }
}
