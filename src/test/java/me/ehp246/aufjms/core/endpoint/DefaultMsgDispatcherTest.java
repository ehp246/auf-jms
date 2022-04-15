package me.ehp246.aufjms.core.endpoint;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.endpoint.FailedInvocation;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.spi.Log4jContext;
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
        final var ref = new FailedInvocation[1];
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
        final var ref = new FailedInvocation[1];
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
        final var threadRef = new Thread[3];
        final var sessionRef = new Session[3];
        final var log4jRef = new ArrayList<Map<String, String>>();

        new DefaultMsgDispatcher(m -> new ExecutableRecord(null, null), (e, c) -> {
            threadRef[0] = Thread.currentThread();
            log4jRef.add(ThreadContext.getContext());
            sessionRef[0] = AufJmsContext.getSession();
            return () -> {
                threadRef[1] = Thread.currentThread();
                sessionRef[1] = AufJmsContext.getSession();
                return InvocationOutcome.thrown(new RuntimeException());
            };
        }, null, m -> null, m -> {
            threadRef[2] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
        }).onMessage(new MockTextMessage(), session);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(threadRef[1], threadRef[2]);

        final var after = ThreadContext.getContext();
        for (final var v : Log4jContext.values()) {
            Assertions.assertEquals(true, log4jRef.get(0).containsKey(v.name()));
            Assertions.assertEquals(true, !after.containsKey(v.name()));
        }

        Assertions.assertEquals(session, sessionRef[0]);
        Assertions.assertEquals(session, sessionRef[1]);
        Assertions.assertEquals(session, sessionRef[2]);
        Assertions.assertEquals(null, AufJmsContext.getSession());
    }
    

    @Test
    void thread_02() throws JMSException, InterruptedException, ExecutionException {
        final var threadRef = new Thread[4];
        final var sessionRef = new Session[3];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(() -> Thread.currentThread()).get();

        new DefaultMsgDispatcher(m -> new ExecutableRecord(null, null), (e, c) -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[0] = AufJmsContext.getSession();
            return () -> {
                threadRef[2] = Thread.currentThread();
                sessionRef[1] = AufJmsContext.getSession();
                return InvocationOutcome.thrown(new RuntimeException());
            };
        }, executor, m -> null, m -> {
            threadRef[3] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
        }).onMessage(new MockTextMessage(), session);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], threadRef[1], "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(threadRef[1], threadRef[2]);
        Assertions.assertEquals(threadRef[2], threadRef[3]);

        Assertions.assertEquals(session, sessionRef[0]);
        Assertions.assertEquals(session, sessionRef[1]);
        Assertions.assertEquals(session, sessionRef[2]);
        Assertions.assertEquals(null, AufJmsContext.getSession());
    }

}
