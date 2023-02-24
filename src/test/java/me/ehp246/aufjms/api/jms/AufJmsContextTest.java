package me.ehp246.aufjms.api.jms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.jms.JMSContext;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.Session;

/**
 * @author Lei Yang
 *
 */
class AufJmsContextTest {

    @BeforeEach
    void cleanUp() {
        AufJmsContext.clearSession();
    }

    @Test
    void test_01() {
        Assertions.assertThrows(NullPointerException.class, () -> AufJmsContext.set((Session) null));
    }

    @Test
    void test_02() {
        final var session = Mockito.mock(Session.class);
        AufJmsContext.set(session);
        Assertions.assertEquals(session, AufJmsContext.getSession());
    }

    @Test
    void test_03() {
        AufJmsContext.set(Mockito.mock(Session.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> AufJmsContext.set(Mockito.mock(Session.class)));
    }

    @Test
    void test_04() {
        final var session = Mockito.mock(Session.class);

        AufJmsContext.set(session);

        Assertions.assertEquals(session, AufJmsContext.clearSession());
        Assertions.assertEquals(null, AufJmsContext.getSession());
    }

    @Test
    void jmsContext_01() {
        final var aufJmsContext = AufJmsContext.create();

        Assertions.assertEquals(null, aufJmsContext.getJmsContext());

        final var mock = Mockito.mock(JMSContext.class);
        Assertions.assertEquals(aufJmsContext, aufJmsContext.set(mock));
        Assertions.assertEquals(mock, aufJmsContext.getJmsContext());

        Assertions.assertEquals(null, aufJmsContext.set((JMSContext) null).getJmsContext());
    }

    @Test
    void jmsContext_02() {
        final var context = Mockito.mock(JMSContext.class);
        final var expected = new JMSRuntimeException(null);
        Mockito.doThrow(expected).when(context).close();

        final var actual = Assertions.assertThrows(JMSRuntimeException.class,
                () -> AufJmsContext.create().set(context).close());

        Mockito.verify(context).close();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void jmsContext_03() throws Exception {
        final var context = Mockito.mock(JMSContext.class);

        AufJmsContext.create().set(context).close();

        Mockito.verify(context).close();

        AufJmsContext.create().close();
    }
}
