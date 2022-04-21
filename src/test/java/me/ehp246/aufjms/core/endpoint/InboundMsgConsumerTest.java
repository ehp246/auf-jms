package me.ehp246.aufjms.core.endpoint;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.logging.log4j.ThreadContext;
import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.CompletedInvocation;
import me.ehp246.aufjms.api.endpoint.FailedInvocation;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.AtTopic;
import me.ehp246.aufjms.api.jms.AufJmsContext;
import me.ehp246.aufjms.api.spi.Log4jContext;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;
import me.ehp246.aufjms.util.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class InboundMsgConsumerTest {
    private Session session = Mockito.mock(Session.class);
    private final JmsDispatchFn noopFn = m -> null;

    @Test
    void ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class,
                () -> new InboundMsgConsumer(jmsMsg -> new ExecutableRecord(null, null), (e, c) -> {
                    return () -> InvocationOutcome.thrown(ex);
                }, null, msg -> null, new InvocationListenersSupplier(null, null)).onMessage(new MockTextMessage(),
                        session));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void ex_02() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new InboundMsgConsumer(null, null, null, null, null).onMessage(Mockito.mock(Message.class),
                        session));
    }

    @Test
    void resolver_ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class, () -> new InboundMsgConsumer(jmsMsg -> {
            throw ex;
        }, (e, c) -> () -> InvocationOutcome.returned(null), null, msg -> null, null).onMessage(new MockTextMessage(),
                session));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void unmatched_ex_01() {
        Assertions.assertThrows(UnknownTypeException.class,
                () -> new InboundMsgConsumer(msg -> null, (e, c) -> () -> null, null, msg -> null, null)
                        .onMessage(new MockTextMessage(), session));
    }

    @Test
    void failedMsg_02() throws JMSException {
        final var ref = new FailedInvocation[1];
        final var msg = new MockTextMessage();
        final var ex = new RuntimeException();
        new InboundMsgConsumer(m -> new ExecutableRecord(null, null), (e, c) -> () -> InvocationOutcome.thrown(ex),
                null, m -> null, new InvocationListenersSupplier(null, m -> {
                    ref[0] = m;
                })).onMessage(msg, session);

        Assertions.assertEquals(ex, ref[0].thrown(), "should be the one thrown by application code");
    }

    @Test
    void failedMsg_03() throws JMSException {
        final var ex = new NullPointerException();

        final var t = Assertions.assertThrows(RuntimeException.class,
                () -> new InboundMsgConsumer(m -> new ExecutableRecord(null, null),
                        (e, c) -> () -> InvocationOutcome.thrown(new RuntimeException()), null, m -> null,
                        new InvocationListenersSupplier(null, m -> {
                            throw ex;
                        })).onMessage(new MockTextMessage(), session),
                "should allow the consumer to throw");
        Assertions.assertEquals(t, ex);
    }

    @Test
    void failedMsg_04() throws JMSException {
        final var ref = new FailedInvocation[1];
        final var ex = new RuntimeException();

        final var t = Assertions.assertThrows(RuntimeException.class,
                () -> new InboundMsgConsumer(m -> new ExecutableRecord(null, null),
                        (e, c) -> () -> InvocationOutcome.thrown(ex), null, m -> null,
                        new InvocationListenersSupplier(null, m -> {
                            ref[0] = m;
                            throw new RuntimeException(m.thrown());
                        })).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(t.getCause(), ex, "should be from the consumer");
        Assertions.assertEquals(t.getCause(), ref[0].thrown(), "should allow the consumer to throw");
    }

    @Test
    void thread_01() throws JMSException {
        final var msg = new MockTextMessage(UUID.randomUUID().toString());
        final var threadRef = new Thread[3];
        final var sessionRef = new Session[3];
        final var log4jRef = new ArrayList<Map<String, String>>();

        new InboundMsgConsumer(m -> new ExecutableRecord(null, null), (e, c) -> {
            threadRef[0] = Thread.currentThread();
            log4jRef.add(ThreadContext.getContext());
            sessionRef[0] = AufJmsContext.getSession();
            return () -> {
                threadRef[1] = Thread.currentThread();
                sessionRef[1] = AufJmsContext.getSession();
                return InvocationOutcome.thrown(new RuntimeException());
            };
        }, null, m -> null, new InvocationListenersSupplier(null, m -> {
            threadRef[2] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
        })).onMessage(msg, session);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(threadRef[1], threadRef[2]);

        final var log4jMap = log4jRef.get(0);
        final var after = ThreadContext.getContext();
        for (final var v : Log4jContext.values()) {
            Assertions.assertEquals(true, log4jMap.containsKey(v.name()));
            Assertions.assertEquals(true, !after.containsKey(v.name()));
        }

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
    void thread_02() throws JMSException, InterruptedException, ExecutionException {
        final var threadRef = new Thread[4];
        final var sessionRef = new Session[3];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(() -> Thread.currentThread()).get();

        new InboundMsgConsumer(m -> new ExecutableRecord(null, null), (e, c) -> {
            threadRef[1] = Thread.currentThread();
            sessionRef[0] = AufJmsContext.getSession();
            return () -> {
                threadRef[2] = Thread.currentThread();
                sessionRef[1] = AufJmsContext.getSession();
                return InvocationOutcome.thrown(new RuntimeException());
            };
        }, executor, m -> null, new InvocationListenersSupplier(null, m -> {
            threadRef[3] = Thread.currentThread();
            sessionRef[2] = AufJmsContext.getSession();
        })).onMessage(new MockTextMessage(), session);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], threadRef[1],
                "should be the same thread for binding, action, failed msg consumer");
        Assertions.assertEquals(threadRef[1], threadRef[2]);
        Assertions.assertEquals(threadRef[2], threadRef[3]);

        Assertions.assertEquals(session, sessionRef[0]);
        Assertions.assertEquals(session, sessionRef[1]);
        Assertions.assertEquals(session, sessionRef[2]);
        Assertions.assertEquals(null, AufJmsContext.getSession());
    }

    @Test
    void completed_01() throws InterruptedException, ExecutionException, JMSException {
        final var threadRef = new Thread[1];
        final var completedThread = new Thread[1];
        final var returned = new RuntimeException();
        final var completedRef = new CompletedInvocation[1];

        final var executor = Executors.newSingleThreadExecutor();
        threadRef[0] = executor.submit(() -> Thread.currentThread()).get();

        new InboundMsgConsumer(m -> new ExecutableRecord(null, null), (e, c) -> {
            return () -> {
                return InvocationOutcome.returned(returned);
            };
        }, executor, m -> null, new InvocationListenersSupplier(c -> {
            completedRef[0] = c;
            completedThread[0] = Thread.currentThread();
        }, null)).onMessage(new MockTextMessage(), session);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertEquals(threadRef[0], completedThread[0]);
        Assertions.assertEquals(returned, completedRef[0].returned());
    }

    @Test
    void completed_02() throws JMSException {
        final var expected = new RuntimeException("Completed should not re-throw");

        final var actual = Assertions.assertThrows(RuntimeException.class,
                () -> new InboundMsgConsumer(m -> new ExecutableRecord(null, null),
                        (e, c) -> () -> InvocationOutcome.returned(null), null, noopFn,
                        new InvocationListenersSupplier(c -> {
                            throw expected;
                        }, null)).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void reply_01() throws JMSException {
        final var dispatchRef = new JmsDispatch[1];
        final var expectedBody = new Object();
        final var expectedId = UUID.randomUUID().toString();

        final var message = new MockTextMessage("t1") {
            @Override
            public String getJMSCorrelationID() throws JMSException {
                return expectedId;
            }

            @Override
            public Destination getJMSReplyTo() throws JMSException {
                final var mock = Mockito.mock(Queue.class);
                Mockito.when(mock.getQueueName()).thenReturn("q1");
                return mock;
            }

        };

        new InboundMsgConsumer(m -> new ExecutableRecord(null, null),
                (e, c) -> () -> InvocationOutcome.returned(expectedBody), null, dispatch -> {
                    dispatchRef[0] = dispatch;
                    return null;
                }, new InvocationListenersSupplier(null, null)).onMessage(message, session);

        Assertions.assertEquals(true, dispatchRef[0].to() instanceof AtQueue);
        Assertions.assertEquals("t1", dispatchRef[0].type());
        Assertions.assertEquals(expectedId, dispatchRef[0].correlationId());
        Assertions.assertEquals(expectedBody, dispatchRef[0].body());
    }

    @Test
    void reply_02() throws JMSException {
        final var dispatchRef = new JmsDispatch[1];

        final var message = new MockTextMessage() {

            @Override
            public String getJMSCorrelationID() throws JMSException {
                return null;
            }

            @Override
            public Destination getJMSReplyTo() throws JMSException {
                final var mock = Mockito.mock(Topic.class);
                Mockito.when(mock.getTopicName()).thenReturn("t1");
                return mock;
            }

        };

        new InboundMsgConsumer(m -> new ExecutableRecord(null, null), (e, c) -> () -> InvocationOutcome.returned(null),
                null, dispatch -> {
                    dispatchRef[0] = dispatch;
                    return null;
                }, new InvocationListenersSupplier(null, null)).onMessage(message, session);

        Assertions.assertEquals(true, dispatchRef[0].to() instanceof AtTopic);
        Assertions.assertEquals(null, dispatchRef[0].type());
        Assertions.assertEquals(null, dispatchRef[0].correlationId());
        Assertions.assertEquals(null, dispatchRef[0].body());
    }

    @Test
    void reply_03() throws JMSException {
        final var expected = new JMSException("");

        final var message = new MockTextMessage() {
            @Override
            public Destination getJMSReplyTo() throws JMSException {
                final var mock = Mockito.mock(Topic.class);
                Mockito.when(mock.getTopicName()).thenThrow(expected);
                return mock;
            }

        };

        final var actual = Assertions.assertThrows(JMSRuntimeException.class,
                () -> new InboundMsgConsumer(m -> new ExecutableRecord(null, null),
                        (e, c) -> () -> InvocationOutcome.returned(null), null, dispatch -> null,
                        new InvocationListenersSupplier(null, null)).onMessage(message, session));

        Assertions.assertEquals(expected, actual.getCause());
    }
}
