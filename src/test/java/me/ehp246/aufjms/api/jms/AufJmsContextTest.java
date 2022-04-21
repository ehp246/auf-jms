package me.ehp246.aufjms.api.jms;

import javax.jms.Session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        Assertions.assertThrows(NullPointerException.class, () -> AufJmsContext.set(null));
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
        Assertions.assertThrows(RuntimeException.class, () -> AufJmsContext.set(Mockito.mock(Session.class)));
    }

    @Test
    void test_04() {
        final var session = Mockito.mock(Session.class);

        AufJmsContext.set(session);

        Assertions.assertEquals(session, AufJmsContext.clearSession());
        Assertions.assertEquals(null, AufJmsContext.getSession());
    }
}
