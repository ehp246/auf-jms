package me.ehp246.aufjms.integration.dispatch.body;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.spi.FromJson;
import me.ehp246.aufjms.integration.dispatch.body.AppConfig.BodyAsTypeCase01;
import me.ehp246.aufjms.integration.dispatch.body.AppConfig.BodyCase01;
import me.ehp246.aufjms.integration.dispatch.body.AppConfig.BodyPublisherCase01;
import me.ehp246.aufjms.integration.dispatch.body.JsonAsType.Person;
import me.ehp246.aufjms.integration.dispatch.body.JsonAsType.PersonDob;
import me.ehp246.aufjms.integration.dispatch.body.JsonAsType.PersonName;
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
    private FromJson fromJson;
    @Autowired
    private TestQueueListener listener;
    @Autowired
    private BodyCase01 case01;
    @Autowired
    private BodyPublisherCase01 pubCase01;
    @Autowired
    private BodyAsTypeCase01 asTypeCase01;

    @BeforeEach
    void reset() {
        listener.reset();
    }

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
        // Assertions.assertEquals(false, text.contains(toJson.apply(List.of(-1))),
        // "should be implemented");
    }

    @Test
    void bodyPublisher_01() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        pubCase01.send(expected::toString);

        Assertions.assertEquals(expected, listener.takeReceived().getBody(String.class));
    }

    @Test
    void bodyPublisher_02() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        pubCase01.send(expected);

        Assertions.assertEquals("\"" + expected + "\"", listener.takeReceived().getBody(String.class));
    }

    @Test
    void bodyAsType_01() throws JMSException {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();

        final var now = Instant.now();
        asTypeCase01.ping(new Person(firstName, lastName, now));

        Assertions.assertEquals("{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"dob\":\""
                + now.toString() + "\"}", ((TextMessage) listener.takeReceived()).getText());
    }

    @Test
    void bodyAsType_02() throws JMSException {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();

        final var now = Instant.now();
        final var expected = new Person(firstName, lastName, now);
        asTypeCase01.ping((PersonName) expected);

        final var text = ((TextMessage) listener.takeReceived()).getText();
        final var actual = (Person)(fromJson.apply(text, List.of(() -> Person.class)).get(0));

        Assertions.assertEquals(firstName, actual.firstName());
        Assertions.assertEquals(lastName, actual.lastName());
        Assertions.assertEquals(null, actual.dob());
    }

    @Test
    void bodyAsType_03() throws JMSException {
        final var now = Instant.now();
        final var expected = new Person(null, null, now);
        asTypeCase01.ping((PersonDob) expected);

        final var text = ((TextMessage) listener.takeReceived()).getText();
        final var actual = (Person) (fromJson.apply(text, List.of(() -> Person.class)).get(0));

        Assertions.assertEquals(null, actual.firstName());
        Assertions.assertEquals(null, actual.lastName());
        Assertions.assertEquals(now.toString(), actual.dob().toString());
    }
}
