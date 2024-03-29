package me.ehp246.aufjms.core.inbound;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.logging.log4j.ThreadContext;
import org.jgroups.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocableBinder;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnFailed;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnInvoking;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.aufjms.core.util.TextJmsMsg;
import me.ehp246.aufjms.provider.jackson.JsonByObjectMapper;
import me.ehp246.test.TestUtil;
import me.ehp246.test.TimingExtension;
import me.ehp246.test.mock.InvocableRecord;
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
    private final Invocable invocable = Mockito.mock(Invocable.class);

    private static InvocableBinder bindToBound(final BoundInvocable bound, final Completed completed) {
        Mockito.when(bound.invoke()).thenReturn(completed);

        return (i, m) -> bound;
    }

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
    void beforeEach() throws JMSException {
        ThreadContext.clearAll();
    }

    @Test
    void invoking_01() throws InterruptedException, ExecutionException, JMSException {
        final var threadRef = new Thread[1];
        final var invokingThread = new Thread[1];
        final var bound = Mockito.mock(BoundInvocable.class);
        final var boundRef = new BoundInvocable[1];
        final var executor = Executors.newSingleThreadExecutor();

        threadRef[0] = executor.submit(Thread::currentThread).get();

        new DefaultInvocableDispatcher(bindToBound(bound, Mockito.mock(Completed.class)),
                List.of((InvocationListener.OnInvoking) b -> {
                    boundRef[0] = b;
                    invokingThread[0] = Thread.currentThread();
                }), executor).dispatch(invocable, msg);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], invokingThread[0]);
        Assertions.assertEquals(bound, boundRef[0]);
    }

    @Test
    void invoking_02() throws Throwable {
        final var expected = new RuntimeException();
        final var invoking = Mockito.mock(OnInvoking.class);
        final var completed = Mockito.mock(OnCompleted.class);
        final var failed = Mockito.mock(OnFailed.class);
        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(
                        bindToBound(Mockito.mock(BoundInvocable.class), Mockito.mock(Completed.class)),
                        List.of((InvocationListener.OnInvoking) b -> {
                            throw expected;
                        }, invoking, completed, failed), null).dispatch(invocable, msg));

        Assertions.assertEquals(actual, expected, "should be the thrown from invocable");

        Mockito.verify(invoking, never()).onInvoking(Mockito.any());
        Mockito.verify(completed, never()).onCompleted(Mockito.any());
        Mockito.verify(failed, never()).onFailed(Mockito.any());
    }

    @Test
    void ex_invocable_01() {
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(bindToFail(expected), null, null).dispatch(invocable, msg));

        Assertions.assertEquals(actual, expected, "should be the thrown from invocable");
    }

    @Test
    void failed_02() throws JMSException {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        final var binder = bindToFail(expected);

        final var threw = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(binder, List.of((InvocationListener.OnFailed) m -> {
                    ref[0] = m;
                }), null).dispatch(invocable, msg));

        final var failed = ref[0];

        Assertions.assertEquals(expected, failed.thrown(), "should be the one thrown by application code");
        Assertions.assertEquals(expected, threw);
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

        Assertions.assertEquals(expected, actual.getSuppressed()[0], "should have it as suppressed");
    }

    @Test
    void failed_04() throws JMSException {
        final var ref = new Failed[2];
        final var failure = new RuntimeException();
        final var supressed = new NullPointerException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvocableDispatcher(bindToFail(failure), List.of((InvocationListener.OnFailed) m -> {
                    ref[0] = m;
                    throw supressed;
                }, (InvocationListener.OnFailed) m -> {
                    ref[1] = m;
                    throw supressed;
                }), null).dispatch(invocable, msg));

        Assertions.assertEquals(failure, actual, "should be from the invoker");
        Assertions.assertEquals(actual, ref[0].thrown(), "should call with best effort");
        Assertions.assertEquals(actual, ref[1].thrown(), "should call with best effort");
        Assertions.assertEquals(actual.getSuppressed().length, 2);
        Assertions.assertEquals(actual.getSuppressed()[0], supressed);
        Assertions.assertEquals(actual.getSuppressed()[1], supressed);
    }

    @Test
    void thread_01() throws JMSException {
        // Binder, listeners
        final var threadRef = new Thread[2];

        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableDispatcher((i, m) -> {
            threadRef[0] = Thread.currentThread();
            final var bound = Mockito.mock(BoundInvocable.class);
            Mockito.when(bound.invoke()).thenReturn(new Failed() {
                private final Exception e = new IllegalArgumentException();

                @Override
                public BoundInvocable bound() {
                    return bound;
                }

                @Override
                public Throwable thrown() {
                    return e;
                }
            });
            return bound;
        }, List.of((InvocationListener.OnFailed) m -> {
            threadRef[1] = Thread.currentThread();
        }), null).dispatch(invocable, msg));

        Assertions.assertEquals(threadRef[0], threadRef[1], "should be the same thread for binder, failed listener");
    }

    @Test
    void thread_02() throws InterruptedException, ExecutionException {
        // Executor, binder, listener
        final var threadRef = new Thread[3];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(Thread::currentThread).get();

        new DefaultInvocableDispatcher((i, m) -> {
            threadRef[1] = Thread.currentThread();
            final var bound = Mockito.mock(BoundInvocable.class);
            Mockito.when(bound.invoke()).thenReturn(Mockito.mock(Completed.class));
            return bound;
        }, List.of((InvocationListener.OnCompleted) m -> {
            threadRef[2] = Thread.currentThread();
        }), executor).dispatch(invocable, msg);

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(threadRef[1], threadRef[2]);
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
                () -> new DefaultInvocableDispatcher(bindToFail(new RuntimeException()), null, null).dispatch(invocable,
                        msg));

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
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableDispatcher((i, m) -> {
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

    @Test
    void log4jConext_01() {
        final var contextRef = new Map[2];
        final var key = UUID.randomUUID().toString();
        final var context = Map.of(key, UUID.randomUUID().toString());

        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableDispatcher((i, m) -> {
            final var bound = Mockito.mock(BoundInvocable.class);
            Mockito.when(bound.mdc()).thenReturn(context);
            Mockito.when(bound.invoke()).then(new Answer<Object>() {

                @Override
                public Object answer(final InvocationOnMock invocation) throws Throwable {
                    contextRef[0] = ThreadContext.getContext();
                    return new Failed() {
                        private final Exception e = new IllegalArgumentException();

                        @Override
                        public BoundInvocable bound() {
                            return bound;
                        }

                        @Override
                        public Throwable thrown() {
                            return e;
                        }
                    };
                }
            });
            return bound;
        }, List.of((InvocationListener.OnFailed) m -> {
            contextRef[1] = ThreadContext.getContext();
        }), null).dispatch(invocable, msg));

        Assertions.assertEquals(null, ThreadContext.get(key), "should clean up");
        Assertions.assertEquals(context.get(key), contextRef[0].get(key), "should be there for the invoke");
        Assertions.assertEquals(context.get(key), contextRef[1].get(key), "should be there for the listeners");
    }

    @Test
    void log4jConext_02() {
        final var contextRef = new Map[2];

        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableDispatcher((i, m) -> {
            final var bound = Mockito.mock(BoundInvocable.class);
            Mockito.when(bound.invoke()).then(new Answer<Object>() {

                @Override
                public Object answer(final InvocationOnMock invocation) throws Throwable {
                    contextRef[0] = ThreadContext.getContext();
                    return new Failed() {
                        private final Exception e = new IllegalArgumentException();

                        @Override
                        public BoundInvocable bound() {
                            return bound;
                        }

                        @Override
                        public Throwable thrown() {
                            return e;
                        }
                    };
                }
            });
            return bound;
        }, List.of((InvocationListener.OnFailed) m -> {
            contextRef[1] = ThreadContext.getContext();
        }), null).dispatch(invocable, msg));

        Assertions.assertEquals(0, ThreadContext.getContext().size());
        Assertions.assertEquals(0, contextRef[0].size());
        Assertions.assertEquals(0, contextRef[1].size());
    }
}
