package me.ehp246.aufjms.core.dispatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.provider.jackson.JsonByObjectMapper;
import me.ehp246.test.Jackson;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultProxyReturnParserTest {
    private final JsonByObjectMapper jsonService = Jackson.jsonService();
    private final DefaultProxyReturnParser parser = new DefaultProxyReturnParser(jsonService);

    @Test
    void local_01() {
        final var captor = TestUtil.newCaptor(ReturnCases.VoidCase01.class);

        captor.proxy().m01();
        final var binder = parser.parse(captor.invocation().method());

        Assertions.assertEquals(true, binder instanceof LocalReturnBinder);

        final var localBinder = (LocalReturnBinder) binder;

        Assertions.assertEquals(null, localBinder.apply(null));
        Assertions.assertEquals(null, localBinder.apply(Mockito.mock(JmsDispatch.class)));
    }

    @Test
    void remote_01() {
        final var captor = TestUtil.newCaptor(ReturnCases.ReturnCase01.class);

        captor.setReturn(0);
        captor.proxy().m01();

        final var binder = parser.parse(captor.invocation().method());

        Assertions.assertEquals(true, binder instanceof RemoteReturnBinder);

        final var remoteBinder = (RemoteReturnBinder) binder;

        final var mockMsg = Mockito.mock(JmsMsg.class);
        Mockito.when(mockMsg.text()).thenReturn("10");

        Assertions.assertEquals(10, remoteBinder.apply(null, mockMsg));
    }
}
