package me.ehp246.test.embedded.dispatch.property;

import java.util.Collections;
import java.util.Map;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.jms.JMSException;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;
import me.ehp246.test.embedded.dispatch.property.AppConfig.Case01;
import me.ehp246.test.embedded.dispatch.property.AppConfig.Case02;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, TestQueueListener.class, EmbeddedArtemisConfig.class }, properties = {
        "app.version=AufJms.2.0" })
class PropertyTest {
    @Autowired
    private Case01 case01;
    @Autowired
    private Case02 case02;
    @Autowired
    private TestQueueListener listener;

    @SuppressWarnings("unchecked")
    @Test
    void test_01() throws JMSException {
        case01.ping();

        // JMSXDeliveryCount
        Assertions.assertEquals(1, Collections.list(listener.take().getPropertyNames()).size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_02() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        case01.ping(expected);

        final var message = listener.take();

        Assertions.assertEquals(2, Collections.list(message.getPropertyNames()).size());
        Assertions.assertEquals(expected, message.getStringProperty("AppName"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_03() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        case01.ping(Map.of("mapName", expected));

        final var message = listener.take();

        Assertions.assertEquals(2, Collections.list(message.getPropertyNames()).size());
        Assertions.assertEquals(expected, message.getStringProperty("mapName"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_04() throws JMSException {
        final var expected0 = UUID.randomUUID().toString();
        final var expected1 = expected0;

        case01.ping(expected0, expected1);

        final var message = listener.take();

        Assertions.assertEquals(3, Collections.list(message.getPropertyNames()).size());
        Assertions.assertEquals(expected0, message.getStringProperty("AppName"));
        Assertions.assertEquals(expected1, message.getStringProperty("appName"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_05() throws JMSException {
        case01.ping((String) null);

        final var message = listener.take();

        Assertions.assertEquals(2, Collections.list(message.getPropertyNames()).size());
        Assertions.assertEquals(null, message.getStringProperty("appName"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_06() throws JMSException {
        case02.ping();

        final var message = listener.take();

        Assertions.assertEquals(3, Collections.list(message.getPropertyNames()).size());
        Assertions.assertEquals("AufJms", message.getStringProperty("appName"));
        Assertions.assertEquals("AufJms.2.0", message.getStringProperty("appVersion"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_07() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        case02.ping(expected);

        final var message = listener.take();

        Assertions.assertEquals(4, Collections.list(message.getPropertyNames()).size());
        Assertions.assertEquals(expected, message.getStringProperty("AppName"));
        Assertions.assertEquals("AufJms", message.getStringProperty("appName"));
        Assertions.assertEquals("AufJms.2.0", message.getStringProperty("appVersion"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_08() throws JMSException {
        final var expected = UUID.randomUUID().toString();

        case02.ping(Map.of("appName", expected, "appVersion", "1.0"));

        final var message = listener.take();

        Assertions.assertEquals(3, Collections.list(message.getPropertyNames()).size());
        Assertions.assertEquals(expected, message.getStringProperty("appName"));
        Assertions.assertEquals("1.0", message.getStringProperty("appVersion"));
    }
}
