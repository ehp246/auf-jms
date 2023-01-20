package me.ehp246.aufjms.core.inbound;

import static org.mockito.Mockito.times;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocableBinder;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.inbound.DefaultInvocableBinder;
import me.ehp246.aufjms.core.inbound.DefaultInvocableDispatcher;
import me.ehp246.aufjms.core.inbound.InvocableRecord;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.aufjms.core.util.TextJmsMsg;
import me.ehp246.aufjms.provider.jackson.JsonByObjectMapper;
import me.ehp246.test.TestUtil;
import me.ehp246.test.TimingExtension;
import me.ehp246.test.mock.MockJmsMsg;
import me.ehp246.test.mock.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
class DefaultInvocableDispatcherTest {
    private final static int LOOP = 1_000_000;
    private final TextMessage message = new MockTextMessage();
    private final JmsMsg msg = TextJmsMsg.from(message);
    private final Session session = Mockito.mock(Session.class);
    private final Invocable invocable = Mockito.mock(Invocable.class);
    private static InvocableBinder bindToComplete(final Completed completed) {
        final var bound = Mockito.mock(BoundInvocable.class);
        Mockito.when(bound.invoke()).thenReturn(completed);

        return (i, m) -> bound;
    }

    private static InvocableBinder bindToFail(final Exception ex) {
        final BoundInvocable bound = Mockito.mock(BoundInvocable.class);

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
        Mockito.when(bound.invoke()).thenReturn(failed);

        return (i, m) -> bound;
    }

    @BeforeEach
    void setup() {
        AufJmsContext.clearSession();
    }

    @Test
    void ex_invocable_01() {
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(bindToFail(expected), null, null).dispatch(invocable,
                        msg));

        Assertions.assertEquals(actual, expected, "should be the thrown from invocable");
    }

    @Test
    void failed_02() throws JMSException {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        final var binder = bindToFail(expected);
        new DefaultInvocableDispatcher(binder, List.of((InvocationListener.OnFailed) m -> {
            ref[0] = m;
        }), null).dispatch(invocable, msg);

        final var failed = ref[0];

        Assertions.assertEquals(expected, failed.thrown(), "should be the one thrown by application code");
        Assertions.assertEquals(binder.bind(null, null), failed.bound());
    }

    @Test
    void failed_03() throws JMSException {
        final var expected = new NullPointerException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(bindToFail(new IllegalArgumentException()),
                List.of((InvocationListener.OnFailed) m -> {
                    throw expected;
                        }), null).dispatch(invocable, msg),
                "should allow the listener to throw back to the broker");

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void failed_04() throws JMSException {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(bindToFail(expected),
                List.of((InvocationListener.OnFailed) m -> {
                    ref[0] = m;
                    throw (RuntimeException) (m.thrown());
                        }), null).dispatch(invocable, msg));

        Assertions.assertEquals(actual, expected, "should be from the invoker");
        Assertions.assertEquals(actual, ref[0].thrown(), "should allow the listener to throw");
    }

    @Test
    void thread_01() throws JMSException {
        // Binder, listeners
        final var threadRef = new Thread[2];
        final var sessionRef = new Session[2];

        AufJmsContext.set(session);

        new DefaultInvocableDispatcher((i, m) -> {
            threadRef[0] = Thread.currentThread();
            sessionRef[0] = AufJmsContext.getSession();
            final var bound = Mockito.mock(BoundInvocable.class);
            Mockito.when(bound.invoke()).thenReturn(Mockito.mock(Failed.class));
            return bound;
        }, List.of((InvocationListener.OnFailed) m -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[1] = AufJmsContext.getSession();
        }), null).dispatch(invocable, msg);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binder, failed listener");

        Assertions.assertEquals(session, sessionRef[0]);
        Assertions.assertEquals(session, sessionRef[1]);
    }

    @Test
    void thread_02() throws InterruptedException, ExecutionException {
        // Executor, binder, listener
        final var threadRef = new Thread[3];
        final var sessionRef = new Session[3];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(Thread::currentThread).get();

        // Should not show up in the executor
        AufJmsContext.set(session);

        new DefaultInvocableDispatcher((i, m) -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[1] = AufJmsContext.getSession();
            final var bound = Mockito.mock(BoundInvocable.class);
            Mockito.when(bound.invoke()).thenReturn(Mockito.mock(Completed.class));
            return bound;
        }, List.of((InvocationListener.OnCompleted) m -> {
            threadRef[2] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
        }), executor).dispatch(invocable, msg);

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(threadRef[1], threadRef[2]);

        // 0 not used
        Assertions.assertEquals(null, sessionRef[1]);
        Assertions.assertEquals(null, sessionRef[2]);
    }

    @Test
    void completed_01() throws InterruptedException, ExecutionException, JMSException {
        final var threadRef = new Thread[1];
        final var completedThread = new Thread[1];
        final var completed = Mockito.mock(Completed.class);
        final var completedRef = new Completed[1];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(Thread::currentThread).get();

        new DefaultInvocableDispatcher(bindToComplete(completed), List.of((InvocationListener.OnCompleted) c -> {
            completedRef[0] = c;
            completedThread[0] = Thread.currentThread();
        }), executor).dispatch(invocable, msg);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], completedThread[0]);
        Assertions.assertEquals(completed, completedRef[0]);
    }

    @Test
    void completed_02() throws JMSException {
        final var expected = new RuntimeException("Completed");

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(bindToComplete(Mockito.mock(Completed.class)),
                List.of((InvocationListener.OnCompleted) c -> {
                    throw expected;
                        }), null).dispatch(invocable, msg));

        Assertions.assertEquals(expected, actual, "should be thrown the broker");
    }

    @Test
    void completed_close_01() throws Exception {
        final var completed = Mockito.mock(OnCompleted.class);

        Mockito.doThrow(new IllegalStateException("Don't close me")).when(invocable).close();

        new DefaultInvocableDispatcher(bindToComplete(Mockito.mock(Completed.class)), List.of(completed), null)
                .dispatch(invocable, msg);

        Mockito.verify(invocable, times(1)).close();
        // Exception from the close should be suppressed.
        Mockito.verify(completed, times(1)).onCompleted(Mockito.any(Completed.class));
    }

    @Test
    void close_01() throws Exception {
        new DefaultInvocableDispatcher(bindToComplete(Mockito.mock(Completed.class)), null, null).dispatch(invocable,
                msg);

        // Should close on completed invocation
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_02() throws Exception {
        Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(bindToFail(new RuntimeException()), null, null)
                        .dispatch(invocable, msg));

        // Should close on failed invocation
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_03() throws Exception {
        Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher((i, m) -> null, null, null).dispatch(invocable, msg));

        // Should close on wrong data
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_04() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultInvocableDispatcher((i, m) -> {
                    throw new IllegalArgumentException();
                }, null, null).dispatch(invocable, msg));

        // Should close on binder exception
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.perf", matches = "true")
    void perf_01() {
        final var binder = new DefaultInvocableBinder(new JsonByObjectMapper(TestUtil.OBJECT_MAPPER));
        final var dispatcher = new DefaultInvocableDispatcher(binder, null, null);
        final var msg = new MockJmsMsg();
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.PerfCase(),
                new ReflectedType<>(InvocableBinderTestCases.PerfCase.class).findMethods("m01").get(0));

        IntStream.range(0, LOOP).forEach(i -> dispatcher.dispatch(invocable, msg));
    }
}
