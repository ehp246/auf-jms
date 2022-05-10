package me.ehp246.aufjms.core.endpoint;

import static org.mockito.Mockito.times;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.ThreadContext;
import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.BoundInvoker;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.endpoint.MsgInvocableFactory;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.util.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class InboundMsgConsumerTest {
    private final TextMessage message = new MockTextMessage();
    private final Invocable invocable = Mockito.mock(Invocable.class);
    private MsgInvocableFactory factory = msg -> invocable;
    private InvocableBinder binder = Mockito.mock(InvocableBinder.class);
    private BoundInvoker invoker = b -> Mockito.mock(Completed.class);
    private BoundInvocable bound = Mockito.mock(BoundInvocable.class);
    private Session session = Mockito.mock(Session.class);
    private final JmsDispatchFn noopFn = m -> null;

    @BeforeEach
    void setup() {
        Mockito.when(binder.bind(Mockito.eq(invocable), Mockito.any(MsgContext.class))).thenReturn(bound);
        Mockito.when(bound.invocable()).thenReturn(invocable);
    }

    private static InvocableBinder binder(final Invocable invocable, final BoundInvocable bound) {
        final var binder = Mockito.mock(InvocableBinder.class);

        Mockito.when(binder.bind(Mockito.eq(invocable), Mockito.any(MsgContext.class))).thenReturn(bound);

        return binder;
    }

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

    @Test
    void ex_action_01() {
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new InboundMsgConsumer(factory, binder, failed(bound, expected), null, msg -> null, null)
                        .onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(actual, expected, "should be the thrown from invocable");
    }

    @Test
    void ex_message_02() {
        final var actual = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new InboundMsgConsumer(null, null, invoker, null, null, null)
                        .onMessage(Mockito.mock(Message.class), session));

        Assertions.assertEquals(true, actual.getMessage().startsWith("Un-supported message"));
    }

    @Test
    void ex_factory_01() {
        final var expected = new RuntimeException();
        final var actual = Assertions.assertThrows(RuntimeException.class, () -> new InboundMsgConsumer(jmsMsg -> {
            throw expected;
        }, binder, invoker, null, msg -> null, null).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void ex_message_01() {
        Assertions.assertThrows(UnknownTypeException.class,
                () -> new InboundMsgConsumer(msg -> null, binder, invoker, null, msg -> null, null)
                        .onMessage(new MockTextMessage(), session));
    }

    @Test
    void failed_02() throws JMSException {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        new InboundMsgConsumer(factory, binder(invocable, bound), failed(bound, expected), null, m -> null,
                (InvocationListener.OnFailed) m -> {
                    ref[0] = m;
                }).onMessage(new MockTextMessage(), session);

        final var failed = ref[0];

        Assertions.assertEquals(expected, failed.thrown(), "should be the one thrown by application code");
        Assertions.assertEquals(bound, failed.bound());
    }

    @Test
    void failed_03() throws JMSException {
        final var expected = new NullPointerException();

        final var actual = Assertions.assertThrows(RuntimeException.class, () -> new InboundMsgConsumer(factory, binder,
                b -> Mockito.mock(Failed.class), null, m -> null, (InvocationListener.OnFailed) m -> {
                    throw expected;
                }).onMessage(new MockTextMessage(), session), "should allow the listener to throw back to the broker");

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void failed_04() throws JMSException {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class, () -> new InboundMsgConsumer(factory, binder,
                failed(bound, expected), null, m -> null, (InvocationListener.OnFailed) m -> {
                    ref[0] = m;
                    throw (RuntimeException) (m.thrown());
                }).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(actual, expected, "should be from the invoker");
        Assertions.assertEquals(actual, ref[0].thrown(), "should allow the listener to throw");
    }

    @Test
    void thread_01() throws JMSException {
        final var msg = new MockTextMessage(UUID.randomUUID().toString());
        // Binder, invoker, listeners
        final var threadRef = new Thread[3];
        final var sessionRef = new Session[3];
        final var log4jRef = new ArrayList<Map<String, String>>();

        new InboundMsgConsumer(factory, (i, m) -> {
            threadRef[0] = Thread.currentThread();
            sessionRef[0] = AufJmsContext.getSession();
            log4jRef.add(ThreadContext.getContext());
            return Mockito.mock(BoundInvocable.class);
        }, b -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[1] = AufJmsContext.getSession();
            return Mockito.mock(Failed.class);
        }, null, m -> null, (InvocationListener.OnFailed) m -> {
            threadRef[2] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
        }).onMessage(msg, session);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binder, invoker, failed listener");
        Assertions.assertEquals(threadRef[1], threadRef[2]);

        final var log4jMap = log4jRef.get(0);
        final var after = ThreadContext.getContext();
        for (final var v : Log4jContext.values()) {
            Assertions.assertEquals(true, log4jMap.containsKey(v.name()));
            Assertions.assertEquals(true, !after.containsKey(v.name()));
        }

        // Supported ThreadContext
        Assertions.assertEquals(msg.getJMSCorrelationID(), log4jMap.get(Log4jContext.AufJmsCorrelationId.name()));
        Assertions.assertEquals(msg.getJMSType(), log4jMap.get(Log4jContext.AufJmsType.name()));
        Assertions.assertEquals(msg.getJMSDestination().toString(),
                log4jMap.get(Log4jContext.AufJmsDestination.name()));

        Assertions.assertEquals(session, sessionRef[0]);
        Assertions.assertEquals(session, sessionRef[1]);
        Assertions.assertEquals(session, sessionRef[2]);
        Assertions.assertEquals(null, AufJmsContext.getSession());
    }

    @Test
    void thread_02() throws JMSException, InterruptedException, ExecutionException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        // Executor, binder, invoker, listener
        final var threadRef = new Thread[4];
        final var sessionRef = new Session[4];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(() -> Thread.currentThread()).get();

        new InboundMsgConsumer(factory, (i, m) -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[1] = AufJmsContext.getSession();
            return Mockito.mock(BoundInvocable.class);
        }, b -> {
            threadRef[2] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
            return Mockito.mock(Completed.class);
        }, executor, m -> null, (InvocationListener.OnCompleted) m -> {
            threadRef[3] = Thread.currentThread();
            sessionRef[3] = AufJmsContext.getSession();
        }).onMessage(new MockTextMessage(), session);

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
        Assertions.assertEquals(null, AufJmsContext.getSession());
    }

    @Test
    void completed_01() throws InterruptedException, ExecutionException, JMSException {
        final var threadRef = new Thread[1];
        final var completedThread = new Thread[1];
        final var completed = Mockito.mock(Completed.class);
        final var completedRef = new Completed[1];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(() -> Thread.currentThread()).get();

        new InboundMsgConsumer(factory, binder, b -> completed, executor, m -> null,
                (InvocationListener.OnCompleted) c -> {
                    completedRef[0] = c;
                    completedThread[0] = Thread.currentThread();
                }).onMessage(new MockTextMessage(), session);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], completedThread[0]);
        Assertions.assertEquals(completed, completedRef[0]);
    }

    @Test
    void completed_02() throws JMSException {
        final var expected = new RuntimeException("Completed");

        final var actual = Assertions.assertThrows(RuntimeException.class, () -> new InboundMsgConsumer(factory, binder,
                b -> Mockito.mock(Completed.class), null, noopFn, (InvocationListener.OnCompleted) c -> {
                    throw expected;
                }).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(expected, actual, "should be thrown the broker");
    }

    @Test
    void completed_close_01() throws Exception {
        final var completed = Mockito.mock(OnCompleted.class);

        Mockito.doThrow(new IllegalStateException("Don't close me")).when(invocable).close();

        new InboundMsgConsumer(factory, binder, invoker, null, dispatch -> null, completed)
                .onMessage(new MockTextMessage(), session);

        Mockito.verify(invocable, times(1)).close();
        // Exception from the close should be suppressed.
        Mockito.verify(completed, times(1)).onCompleted(Mockito.any(Completed.class));
    }

    @Test
    void close_01() throws Exception {
        new InboundMsgConsumer(factory, binder, invoker, null, dispatch -> null, null).onMessage(new MockTextMessage(),
                session);

        // Should close on completed invocation
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_02() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> new InboundMsgConsumer(factory, binder,
                failed(bound, new RuntimeException()), null, dispatch -> null, null).onMessage(message, session));

        // Should close on failed invocation
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_03() throws Exception {
        Assertions.assertThrows(RuntimeException.class,
                () -> new InboundMsgConsumer(factory, (i, m) -> null, b -> null, null, dispatch -> null, null)
                        .onMessage(message, session));

        // Should close on wrong data
        Mockito.verify(invocable, times(1)).close();
    }

    @Test
    void close_04() throws Exception {
        Mockito.when(binder.bind(Mockito.any(), Mockito.any())).thenThrow(new IllegalArgumentException());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new InboundMsgConsumer(factory, binder, invoker, null, dispatch -> null, null).onMessage(message,
                        session));

        // Should close on binder exception
        Mockito.verify(invocable, times(1)).close();
    }

}
