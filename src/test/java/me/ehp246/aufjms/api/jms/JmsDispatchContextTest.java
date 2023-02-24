package me.ehp246.aufjms.api.jms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.jms.JMSContext;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchContextTest {
    @Test
    void clear_01() throws Exception {
        final var mock = Mockito.mock(JMSContext.class);

        JmsDispatchContext.set(mock).close();

        Mockito.verify(mock).close();

        Assertions.assertEquals(null, JmsDispatchContext.getJmsContext());
    }

}
