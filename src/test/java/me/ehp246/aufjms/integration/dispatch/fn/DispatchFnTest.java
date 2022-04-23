package me.ehp246.aufjms.integration.dispatch.fn;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufjms.api.dispatch.BodyPublisher;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.core.dispatch.DefaultDispatchFnProvider;
import me.ehp246.aufjms.integration.dispatch.fn.BodyAsType.PersonDob;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class,
        EmbeddedArtemisConfig.class }, webEnvironment = WebEnvironment.NONE)
class DispatchFnTest {
    private static final At TO = At.toQueue(TestQueueListener.DESTINATION_NAME);

    @Autowired
    private TestQueueListener listener;
    @Autowired
    private DefaultDispatchFnProvider fnProvider;
    @Autowired
    private ToJson toJson;
    @Autowired
    private JmsDispatchFn fn;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void test_01() throws JMSException {
        final var fn = fnProvider.get("");

        fn.send(JmsDispatch.toDispatch(TO, null));

        final var message = listener.takeReceived();

        Assertions.assertEquals(null, message.getJMSType());
        Assertions.assertEquals(true, message.getJMSCorrelationID() != null);
    }

    @Test
    void test_02() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();
        final var id = UUID.randomUUID().toString();
        final var body = UUID.randomUUID().toString();

        fn.send(JmsDispatch.toDispatch(TO, type, body, id));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
        Assertions.assertEquals(id, message.getJMSCorrelationID());

        Assertions.assertEquals(toJson.apply(List.of(new ToJson.From(body))), message.getBody(String.class),
                "should be encoded in JSON");
    }

    @Test
    void bodySupplier_01() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();
        final var body = UUID.randomUUID();

        fn.send(JmsDispatch.toDispatch(TO, type, (BodyPublisher) body::toString));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
        Assertions.assertEquals(body.toString(), message.getBody(String.class), "should be as-is");
    }

    @Test
    void bodySupplier_02() throws JMSException {
        final var fn = fnProvider.get("");
        final var expected = UUID.randomUUID().toString();

        fn.send(new JmsDispatch() {

            @Override
            public At to() {
                return TO;
            }

            @Override
            public Object body() {
                return (BodyPublisher) expected::toString;
            }
        });

        Assertions.assertEquals(expected, listener.takeReceived().getBody(String.class));
    }

    @Test
    void bodyAs_01() throws JMSException {
        final var fn = fnProvider.get("");
        final var expected = new BodyAsType.Person(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                Instant.now());

        fn.send(new JmsDispatch() {

            @Override
            public At to() {
                return TO;
            }

            @Override
            public Object body() {
                return expected;
            }

            @Override
            public BodyAs bodyAs() {
                return BodyAs.of(PersonDob.class);
            }

        });

        Assertions.assertEquals("{\"dob\":\"" + expected.dob().toString() + "\"}",
                listener.takeReceived().getBody(String.class));
    }

    @Test
    void properties_01() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();

        fn.send(JmsDispatch.toDispatch(TO, type, null, null, Map.of("p1", "v-1", "p2", "v-2")));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
        Assertions.assertEquals("v-1", message.getStringProperty("p1"));
        Assertions.assertEquals("v-2", message.getStringProperty("p2"));
    }

    @Test
    void properties_02() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();

        fn.send(JmsDispatch.toDispatch(TO, type, null, null, null));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
    }

    @Test
    void properties_03() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();

        fn.send(JmsDispatch.toDispatch(TO, type, null, null, Map.of()));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
    }

    @Test
    void defaultFn_01() {
        Assertions.assertEquals(true, fn != null);
    }
}
