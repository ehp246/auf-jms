package me.ehp246.aufjms.integration.dispatch.fn;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufjms.api.dispatch.BodySupplier;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.To;
import me.ehp246.aufjms.api.jms.ToQueueRecord;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.core.dispatch.DefaultDispatchFnProvider;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class,
        EmbeddedArtemisConfig.class }, webEnvironment = WebEnvironment.NONE)
class DispatchFnTest {
    private static final To to = new ToQueueRecord(TestQueueListener.DESTINATION_NAME);

    @Autowired
    private TestQueueListener listener;
    @Autowired
    private DefaultDispatchFnProvider fnProvider;
    @Autowired
    private ToJson toJson;

    @Test
    void test_01() throws JMSException {
        final var fn = fnProvider.get("");

        fn.send(JmsDispatch.newDispatch(to, null));

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

        fn.send(JmsDispatch.newDispatch(to, type, id, body));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
        Assertions.assertEquals(id, message.getJMSCorrelationID());

        Assertions.assertEquals(toJson.apply(List.of(body)), message.getBody(String.class),
                "should be encoded in JSON");
    }

    @Test
    void bodySupplier_01() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();
        final var body = UUID.randomUUID();

        fn.send(JmsDispatch.newDispatch(to, type, (BodySupplier) body::toString));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
        Assertions.assertEquals(body.toString(), message.getBody(String.class), "should be as-is");
    }
    
    @Test
    void properties_01() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();

        fn.send(JmsDispatch.newDispatch(to, type, null, Map.of("p1", "v-1", "p2", "v-2"), null));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
        Assertions.assertEquals("v-1", message.getStringProperty("p1"));
        Assertions.assertEquals("v-2", message.getStringProperty("p2"));
    }
}
