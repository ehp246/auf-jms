package me.ehp246.aufjms.api.jms;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.jms.JMSContext;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchContextTest {
    @AfterEach
    void clear() {
        JmsDispatchContext.remove();
    }

    @Test
    void clear_01() throws Exception {
        final var mock = Mockito.mock(JMSContext.class);

        JmsDispatchContext.setJmsContext(mock).close();

        Mockito.verify(mock).close();

        Assertions.assertEquals(null, JmsDispatchContext.jmsContext());
    }

    @Test
    void property_01() throws Exception {
        final Map<String, Object> map = Map.of("", "");

        JmsDispatchContext.setProperties(map);

        Assertions.assertEquals(map, JmsDispatchContext.properties());

        JmsDispatchContext.remove();

        Assertions.assertEquals(null, JmsDispatchContext.jmsContext());
    }

    @Test
    void property_02() throws Exception {
        final Map<String, Object> map = Map.of("", "");

        JmsDispatchContext.setProperties(map);

        final var exe = Executors.newSingleThreadExecutor();
        final var ref = new AtomicReference<Map<String, ?>>();

        exe.execute(() -> {
            ref.set(JmsDispatchContext.properties());
        });

        Assertions.assertEquals(null, ref.get());
    }

    @Test
    void property_03() throws Exception {
        final Map<String, Object> map = Map.of("", "");

        try (final var closeable = JmsDispatchContext.setProperties(map)) {
        }

        Assertions.assertEquals(0, JmsDispatchContext.properties().size());
    }
}
