package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.test.mock.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class RequestReplyListenerTest {
    private final Session session = Mockito.mock(Session.class);

    @Test
    void test_01() throws JMSException, InterruptedException, ExecutionException {
        final var future = new CompletableFuture<JmsMsg>();
        final var message = new MockTextMessage();

        final var mockMap = Mockito.mock(ReplyFutureSupplier.class);
        Mockito.when(mockMap.get(Mockito.eq(message.getJMSCorrelationID()))).thenReturn(future);

        new RequestReplyListener(mockMap).onMessage(message, session);

        Assertions.assertEquals(message, future.get().message());
    }

    @Test
    void test_02() throws JMSException, InterruptedException, ExecutionException {
        Assertions
                .assertDoesNotThrow(() -> new RequestReplyListener(Mockito.mock(ReplyFutureSupplier.class))
                        .onMessage(new MockTextMessage(), session), "should not throw if id is not found");
    }
}
