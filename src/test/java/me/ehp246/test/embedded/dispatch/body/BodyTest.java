package me.ehp246.test.embedded.dispatch.body;

import java.time.Instant;
import java.util.Map;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.jms.FromJson;
import me.ehp246.aufjms.api.spi.BodyOfBuilder;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;
import me.ehp246.test.embedded.dispatch.body.AppConfig.BodyAsTypeCase01;
import me.ehp246.test.embedded.dispatch.body.AppConfig.BodyCase01;
import me.ehp246.test.embedded.dispatch.body.AppConfig.BodyPublisherCase01;
import me.ehp246.test.embedded.dispatch.body.AppConfig.ViewCase01;
import me.ehp246.test.embedded.dispatch.body.Payload.Account.Request;
import me.ehp246.test.embedded.dispatch.body.Payload.Person;
import me.ehp246.test.embedded.dispatch.body.Payload.PersonDob;
import me.ehp246.test.embedded.dispatch.body.Payload.PersonName;

/**
 * @author Lei Yang
 *
 */
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
    @Autowired
    private ViewCase01 viewCase01;

    @Test
    void destination_01() {
        case01.ping();

        final var received = listener.take();

        Assertions.assertEquals(true, received != null);
        Assertions.assertEquals(true, received instanceof TextMessage);
    }

    @Test
    void correlId_01() throws JMSException {
        case01.ping();

        final var received = listener.take();

        Assertions.assertEquals(true, received.getJMSCorrelationID() != null);
    }

    @Test
    void replyTo_01() throws JMSException {
        case01.ping();

        final var received = listener.take();

        Assertions.assertEquals(null, received.getJMSReplyTo());
    }

    @Test
    void body_01() throws JMSException {
        case01.ping();

        final var received = (TextMessage) listener.take();

        Assertions.assertEquals(null, received.getText());
    }

    @Test
    void body_02() throws JMSException {
        final var now = Instant.now();
        case01.ping(Map.of("now", now));

        final var received = (TextMessage) listener.take();

        Assertions.assertEquals(true, received.getText().contains(now.toString()));
    }

    @Test
    void body_03() throws JMSException {
        final var now = Instant.now();
        case01.ping(Map.of("now", now), -1);

        final var received = (TextMessage) listener.take();

        final var text = received.getText();
        Assertions.assertEquals(true, text.contains(now.toString()));
        // Assertions.assertEquals(false, text.contains(toJson.apply(List.of(-1))),
        // "should be implemented");
    }

    @Test
    void bodyPublisher_01() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        pubCase01.send(expected::toString);

        Assertions.assertEquals(expected, listener.take().getBody(String.class));
    }

    @Test
    void bodyPublisher_02() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        pubCase01.send(expected);

        Assertions.assertEquals("\"" + expected + "\"", listener.take().getBody(String.class));
    }

    @Test
    void bodyPublisher_03() throws JMSException {
        pubCase01.send(() -> null);

        Assertions.assertEquals(null, listener.take().getBody(String.class));
    }

    @Test
    void bodyAsType_01() throws JMSException {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();

        final var now = Instant.now();
        asTypeCase01.ping(new Person(firstName, lastName, now));

        Assertions.assertEquals("{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"dob\":\""
                + now.toString() + "\"}", ((TextMessage) listener.take()).getText());
    }

    @Test
    void bodyAsType_02() throws JMSException {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();

        final var now = Instant.now();
        final var expected = new Person(firstName, lastName, now);
        asTypeCase01.ping((PersonName) expected);

        final var text = ((TextMessage) listener.take()).getText();
        final var actual = fromJson.apply(text, BodyOfBuilder.of(Person.class));

        Assertions.assertEquals(firstName, actual.firstName());
        Assertions.assertEquals(lastName, actual.lastName());
        Assertions.assertEquals(null, actual.dob());
    }

    @Test
    void bodyAsType_03() throws JMSException {
        final var now = Instant.now();
        final var expected = new Person(null, null, now);
        asTypeCase01.ping((PersonDob) expected);

        final var text = ((TextMessage) listener.take()).getText();
        final var actual = fromJson.apply(text, BodyOfBuilder.of(Person.class));

        Assertions.assertEquals(null, actual.firstName());
        Assertions.assertEquals(null, actual.lastName());
        Assertions.assertEquals(now.toString(), actual.dob().toString());
    }

    @Test
    void view_01() throws JMSException {
        final var request = new Request(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        this.viewCase01.pingWithId(request);

        final var text = ((TextMessage) listener.take()).getText();

        Assertions.assertEquals(false, text.contains(request.password()));
    }

    @Test
    void view_02() throws JMSException {
        final var request = new Request(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        this.viewCase01.pingWithAll(request);

        final var text = ((TextMessage) listener.take()).getText();

        Assertions.assertEquals(true, text.contains(request.id()));
        Assertions.assertEquals(true, text.contains(request.password()));
    }
}
