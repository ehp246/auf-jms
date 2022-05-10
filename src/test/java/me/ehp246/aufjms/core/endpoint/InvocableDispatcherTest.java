package me.ehp246.aufjms.core.endpoint;

import static org.mockito.Mockito.times;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.BoundInvoker;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.util.TextJmsMsg;
import me.ehp246.aufjms.util.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class InvocableDispatcherTest {
    private final TextMessage message = new MockTextMessage();
    private final JmsMsg msg = TextJmsMsg.from(message);
    private final Session session = Mockito.mock(Session.class);
    private final BoundInvocable bound = Mockito.mock(BoundInvocable.class);
    private final Invocable invocable = Mockito.mock(Invocable.class);
    private final InvocableBinder binder = (i, m) -> bound;
    private final BoundInvoker invoker = b -> Mockito.mock(Completed.class);
    private final MsgContext msgCtx = new MsgContext() {

        @Override
        public JmsMsg msg() {
            return msg;
        }

        @Override
        public Session session() {
            return session;
        }

    };

    private static BoundInvoker failed(final BoundInvocable bound, final Exception ex) {
        final var invoker = Mockito.mock(BoundInvoker.class);
        final var failed = new Failed() {

            @Override
            public BoundInvocable bound() {
                return bound;
            }

            @Override
            public Throwable thrown() {
                return ex;
            }
        };
        Mockito.when(invoker.apply(Mockito.eq(bound))).thenReturn(failed);

        return invoker;
    }

    @BeforeEach
    void setup() {
        AufJmsContext.clearSession();
    }

    @Test
    void ex_invocable_01() {
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new InvocableDispatcher(binder, failed(bound, expected), null, null).dispatch(invocable, msgCtx));

        Assertions.assertEquals(actual, expected, "should be the thrown from invocable");
    }

    @Test
    void failed_02() throws JMSException {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        new InvocableDispatcher(binder, failed(bound, expected), List.of((InvocationListener.OnFailed) m -> {
            ref[0] = m;
        }), null).dispatch(invocable, msgCtx);

        final var failed = ref[0];

        Assertions.assertEquals(expected, failed.thrown(), "should be the one thrown by application code");
        Assertions.assertEquals(bound, failed.bound());
    }

    @Test
    void failed_03() throws JMSException {
        final var expected = new NullPointerException();

        final var actual = Assertions.assertThrows(RuntimeException.class, () -> new InvocableDispatcher(binder, b -> Mockito.mock(Failed.class),
                List.of((InvocationListener.OnFailed) m -> {
                    throw expected;
                }), null).dispatch(invocable, msgCtx), "should allow the listener to throw back to the broker");

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void failed_04() throws JMSException {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class, () -> new InvocableDispatcher(binder, failed(bound, expected),
                List.of((InvocationListener.OnFailed) m -> {
                    ref[0] = m;
                    throw (RuntimeException) (m.thrown());
                }), null).dispatch(invocable, msgCtx));

        Assertions.assertEquals(actual, expected, "should be from the invoker");
        Assertions.assertEquals(actual, ref[0].thrown(), "should allow the listener to throw");
    }

    @Test
    void thread_01() throws JMSException {
        // Binder, invoker, listeners
        final var threadRef = new Thread[3];
        final var sessionRef = new Session[3];

        AufJmsContext.set(session);

        new InvocableDispatcher((i, m) -> {
            threadRef[0] = Thread.currentThread();
            sessionRef[0] = AufJmsContext.getSession();
            return Mockito.mock(BoundInvocable.class);
        }, b -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[1] = AufJmsContext.getSession();
            return Mockito.mock(Failed.class);
        }, List.of((InvocationListener.OnFailed) m -> {
            threadRef[2] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
        }), null).dispatch(invocable, msgCtx);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binder, invoker, failed listener");
        Assertions.assertEquals(threadRef[1], threadRef[2]);

        Assertions.assertEquals(session, sessionRef[0]);
        Assertions.assertEquals(session, sessionRef[1]);
        Assertions.assertEquals(session, sessionRef[2]);
    }

    @Test
    void thread_02() throws InterruptedException, ExecutionException {
        // Executor, binder, invoker, listener
        final var threadRef = new Thread[4];
        final var sessionRef = new Session[4];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(() -> Thread.currentThread()).get();

        AufJmsContext.set(session);

        new InvocableDispatcher((i, m) -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[1] = AufJmsContext.getSession();
            return Mockito.mock(BoundInvocable.class);
        }, b -> {
            threadRef[2] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
            return Mockito.mock(Completed.class);
        }, List.of((InvocationListener.OnCompleted) m -> {
            threadRef[3] = Thread.currentThread();
            sessionRef[3] = AufJmsContext.getSession();
        }), executor).dispatch(invocable, msgCtx);

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(threadRef[1], threadRef[2]);
        Assertions.assertEquals(threadRef[2], threadRef[3]);

        // 0 not used
        Assertions.assertEquals(session, sessionRef[1]);
        Assertions.assertEquals(session, sessionRef[2]);
        Assertions.assertEquals(session, sessionRef[3]);
    }

    @Test
    void completed_01() throws InterruptedException, ExecutionException, JMSException {
        final var threadRef = new Thread[1];
        final var completedThread = new Thread[1];
        final var completed = Mockito.mock(Completed.class);
        final var completedRef = new Completed[1];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(() -> Thread.currentThread()).get();

        new InvocableDispatcher(binder, b -> completed, List.of((InvocationListener.OnCompleted) c -> {
            completedRef[0] = c;
            completedThread[0] = Thread.currentThread();
        }), executor).dispatch(invocable, msgCtx);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], completedThread[0]);
        Assertions.assertEquals(completed, completedRef[0]);
    }

    @Test
    void completed_02() throws JMSException {
        final var expected = new RuntimeException("Completed");

        final var actual = Assertions.assertThrows(RuntimeException.class, () -> new InvocableDispatcher(binder, b -> Mockito.mock(Completed.class),
                List.of((InvocationListener.OnCompleted) c -> {
                    throw expected;
                }), null).dispatch(invocable, msgCtx));

        Assertions.assertEquals(expected, actual, "should be thrown the broker");
    }

    @Test
    void completed_close_01() throws Exception {
        final var completed = Mockito.mock(OnCompleted.class);

        Mockito.doThrow(new IllegalStateException("Don't close me")).when(invocable).close();

        new InvocableDispatcher(binder, invoker, List.of(completed), null).dispatch(invocable, msgCtx);

        Mockito.verify(invocable, times(1)).close();
        // Exception from the close should be suppressed.
        Mockito.verify(completed, times(1)).onCompleted(Mockito.any(Completed.class));
    }

    @Test
    void close_01() throws Exception {
        new InvocableDispatcher(binder, invoker, null, null).dispatch(invocable, msgCtx);

        // Should close on completed invocation
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_02() throws Exception {
        Assertions.assertThrows(RuntimeException.class,
                () -> new InvocableDispatcher(binder, failed(bound, new RuntimeException()), null, null)
                        .dispatch(invocable, msgCtx));

        // Should close on failed invocation
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_03() throws Exception {
        Assertions.assertThrows(RuntimeException.class,
                () -> new InvocableDispatcher((i, m) -> null, b -> null, null, null).dispatch(invocable, msgCtx));

        // Should close on wrong data
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_04() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new InvocableDispatcher((i, m) -> {
                    throw new IllegalArgumentException();
                }, invoker, null, null).dispatch(invocable, msgCtx));

        // Should close on binder exception
        Mockito.verify(invocable, times(1)).close();
    }

}
