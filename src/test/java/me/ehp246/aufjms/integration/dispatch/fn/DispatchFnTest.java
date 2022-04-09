package me.ehp246.aufjms.integration.dispatch.fn;

import java.util.List;

import javax.jms.JMSException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufjms.api.jms.To;
import me.ehp246.aufjms.api.jms.ToQueueRecord;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.core.dispatch.DefaultDispatchFnProvider;
import me.ehp246.aufjms.core.util.MockJmsDispatch;
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

        fn.send(new MockJmsDispatch(to));

        final var message = listener.takeReceived();

        Assertions.assertEquals(null, message.getJMSType());
        Assertions.assertEquals(null, message.getJMSCorrelationID());
    }

    @Test
    void test_02() throws JMSException {
        final var fn = fnProvider.get("");
        final var type = UUID.randomUUID().toString();
        final var id = UUID.randomUUID().toString();
        final var body = UUID.randomUUID().toString();

        fn.send(new MockJmsDispatch(to, type, id, body));

        final var message = listener.takeReceived();

        Assertions.assertEquals(type, message.getJMSType());
        Assertions.assertEquals(id, message.getJMSCorrelationID());

        Assertions.assertEquals(toJson.apply(List.of(body)), message.getBody(String.class),
                "should be encoded in JSON");
    }

}
