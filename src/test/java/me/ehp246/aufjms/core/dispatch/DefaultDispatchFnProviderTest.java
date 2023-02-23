package me.ehp246.aufjms.core.dispatch;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.ToJson;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class DefaultDispatchFnProviderTest {
    private final static ToJson toNullJson = (value, info) -> null;

    @BeforeEach
    void beforeAll() throws JMSException {
        AufJmsContext.clearSession();
    }

    @Test
    void cfname_01() {
        final var cfProvider = Mockito.mock(ConnectionFactoryProvider.class);
        when(cfProvider.get("")).thenReturn(Mockito.mock(ConnectionFactory.class));

        new DefaultDispatchFnProvider(cfProvider, toNullJson, null).get("");

        verify(cfProvider, times(1)).get("");
    }

    @Test
    void cfname_02() {
        final var cfProvider = Mockito.mock(ConnectionFactoryProvider.class);

        when(cfProvider.get("")).thenReturn(null);

        Assertions.assertThrows(Exception.class,
                () -> new DefaultDispatchFnProvider(cfProvider, toNullJson, null).get(""));
    }

}
