package me.ehp246.test.embedded.dispatch.type;

import java.util.Map;

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
import me.ehp246.test.embedded.dispatch.type.AppConfig.OfPropertyCase01;

/**
 * @author Lei Yang
 *
 */
@Timeout(5)
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, EmbeddedArtemisConfig.class })
class PropertyTest {
    @Autowired
    private TestQueueListener listener;
    @Autowired
    private OfPropertyCase01 case01;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    void property_01() throws JMSException {
        final var id = UUID.randomUUID().toString();
        case01.ping(id, 10);

        final var received = listener.take();

        Assertions.assertEquals(id, received.getStringProperty("JMSXGroupID"));
        Assertions.assertEquals(10, received.getIntProperty("JMSXGroupSeq"));
    }

    @Test
    void property_02() throws JMSException {
        case01.ping(Map.<String, Object>of("K1", "1", "K2", Integer.valueOf(3)));

        final var received = listener.take();

        Assertions.assertEquals("1", received.getStringProperty("K1"));
        Assertions.assertEquals(3, received.getIntProperty("K2"));
    }
}
