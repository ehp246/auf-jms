package me.ehp246.aufjms.core.util;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Lei Yang
 *
 */
class TextJmsMsgTest {
    private final TextMessage message = Mockito.mock(TextMessage.class);

    @Test
    void enum_01() throws JMSException {
        Mockito.when(message.getStringProperty("prop1")).thenReturn("Value1");

        Assertions.assertEquals(PropertyEnum.Value1, TextJmsMsg.from(message).property("prop1", PropertyEnum.class));
    }

    @Test
    void enum_02() throws JMSException {
        Mockito.when(message.getStringProperty("prop2")).thenReturn("Value2");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> TextJmsMsg.from(message).property("prop2", PropertyEnum.class));
    }
}
