package me.ehp246.aufjms.core.endpoint;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.endpoint.FailedMsg;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;
import me.ehp246.aufjms.util.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class DefaultMsgDispatcherTest {
    private Session session = Mockito.mock(Session.class);

    @Test
    void ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultMsgDispatcher(jmsMsg -> new ExecutableRecord(null, null), (e, c) -> {
                    return () -> InvocationOutcome.thrown(ex);
                }, null, msg -> null, null).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void resolver_ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class, () -> new DefaultMsgDispatcher(jmsMsg -> {
            throw ex;
        }, (e, c) -> () -> InvocationOutcome.returned(null), null, msg -> null, null).onMessage(new MockTextMessage(),
                session));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void unmatched_ex_01() {
        Assertions.assertThrows(UnknownTypeException.class,
                () -> new DefaultMsgDispatcher(msg -> null, (e, c) -> () -> null, null, msg -> null, null)
                        .onMessage(new MockTextMessage(), session));
    }

    @Test
    void failedMsg_02() throws JMSException {
        final var ref = new FailedMsg[1];
        final var msg = new MockTextMessage();
        final var ex = new RuntimeException();
        new DefaultMsgDispatcher(m -> new ExecutableRecord(null, null), (e, c) -> () -> InvocationOutcome.thrown(ex),
                null, m -> null, m -> {
                    ref[0] = m;
                }).onMessage(msg, session);

        Assertions.assertEquals(ex, ref[0].thrown(), "should be the one thrown by application code");
    }

    @Test
    void failedMsg_03() throws JMSException {
        final var ex = new NullPointerException();

        final var t = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultMsgDispatcher(m -> new ExecutableRecord(null, null),
                        (e, c) -> () -> InvocationOutcome.thrown(new RuntimeException()), null, m -> null, m -> {
                            throw ex;
                        }).onMessage(new MockTextMessage(), session),
                "should allow the consumer to throw");
        Assertions.assertEquals(t, ex);
    }

    @Test
    void failedMsg_04() throws JMSException {
        final var ref = new FailedMsg[1];
        final var ex = new RuntimeException();

        final var t = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultMsgDispatcher(m -> new ExecutableRecord(null, null),
                        (e, c) -> () -> InvocationOutcome.thrown(ex), null, m -> null, m -> {
                            ref[0] = m;
                            throw new RuntimeException(m.thrown());
                        }).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(t.getCause(), ex, "should be from the consumer");
        Assertions.assertEquals(t.getCause(), ref[0].thrown(), "should allow the consumer to throw");
    }

    @Test
    void thread_01() throws JMSException {
        final var ref = new Thread[3];

        new DefaultMsgDispatcher(m -> new ExecutableRecord(null, null), (e, c) -> {
            ref[0] = Thread.currentThread();
            return () -> {
                ref[1] = Thread.currentThread();
                return InvocationOutcome.thrown(new RuntimeException());
            };
        }, null, m -> null, m -> {
            ref[2] = Thread.currentThread();
        }).onMessage(new MockTextMessage(), session);

        Assertions.assertEquals(ref[0], ref[1], "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(ref[1], ref[2]);
    }

    @Test
    void thread_02() throws JMSException, InterruptedException, ExecutionException {
        final var ref = new Thread[4];

        final var executor = Executors.newSingleThreadExecutor();
        ref[0] = executor.submit(() -> Thread.currentThread()).get();

        new DefaultMsgDispatcher(m -> new ExecutableRecord(null, null), (e, c) -> {
            ref[1] = Thread.currentThread();
            return () -> {
                ref[2] = Thread.currentThread();
                return InvocationOutcome.thrown(new RuntimeException());
            };
        }, executor, m -> null, m -> {
            ref[3] = Thread.currentThread();
        }).onMessage(new MockTextMessage(), session);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(ref[0], ref[1], "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(ref[1], ref[2]);
        Assertions.assertEquals(ref[2], ref[3]);
    }
}
