package me.ehp246.broker.sb;

import java.util.stream.IntStream;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufjms.api.jms.AufJmsContext;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnabledIfSystemProperty(named = "me.ehp246.broker.sb", matches = "true")
@TestInstance(Lifecycle.PER_CLASS)
class SbTest {
    @Autowired
    private ConnectionFactory cf;
    private Session session;

    @Autowired
    private ToInbox toInbox;

    @BeforeAll
    void setup() throws JMSException {
        session = cf.createConnection().createSession();
    }

    @BeforeEach
    void reset() {
        AufJmsContext.clearSession();
    }

    @AfterAll
    void teardown() throws JMSException {
        session.close();
    }

    @Test
    void send_001() {
        toInbox.ping();
    }

    @Test
    void send_002() {
        IntStream.range(0, 100).forEach(toInbox::ping);
    }

    @Test
    void send_003() throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            toInbox.ping(i);
            // Wait for n minutes.
            Thread.sleep(1 * 60000);
        }
    }

    @Test
    void send_004() throws JMSException {
        AufJmsContext.set(session);

        IntStream.range(0, 300).forEach(toInbox::ping);
    }

}
