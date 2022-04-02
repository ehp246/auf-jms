package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.jms.JMSException;
import javax.jms.Session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.FailedMsg;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;
import me.ehp246.aufjms.util.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class DefaultInvokableDispatcherTest {
    private Session session = Mockito.mock(Session.class);

    @Test
    void ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvokableDispatcher(jmsMsg -> new ExecutableRecord(null, null), (e, c) -> {
                    return () -> {
                        return InvocationOutcome.thrown(ex);
                    };
                }, null, msg -> null, null).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void resolver_ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvokableDispatcher(jmsMsg -> {
                    throw ex;
                }, (e, c) -> () -> InvocationOutcome.returned(null), null, msg -> null, null)
                        .onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void postexecution_01() throws JMSException {
        final var ref = new AtomicReference<ExecutedInstance>();
        new DefaultInvokableDispatcher(msg -> {
            return new Executable() {

                @Override
                public Method method() {
                    return null;
                }

                @Override
                public Object instance() {
                    return null;
                }

                @Override
                public Consumer<ExecutedInstance> executionConsumer() {
                    return ei -> ref.set(ei);
                }
            };
        }, (e, c) -> () -> InvocationOutcome.returned(ref), null, msg -> null, null).onMessage(new MockTextMessage(),
                session);

        Assertions.assertEquals(ref, ref.get().getOutcome().getReturned());
        Assertions.assertEquals(null, ref.get().getOutcome().getThrown());
    }

    @Test
    void postexecution_ex_01() {
        final var ex = new RuntimeException();
        final var ref = new AtomicReference<ExecutedInstance>();
        final var thrown = Assertions.assertThrows(RuntimeException.class, () -> new DefaultInvokableDispatcher(msg -> {
            return new Executable() {

                @Override
                public Method method() {
                    return null;
                }

                @Override
                public Object instance() {
                    return null;
                }

                @Override
                public Consumer<ExecutedInstance> executionConsumer() {
                    return ei -> ref.set(ei);
                }
            };
        }, (e, c) -> () -> InvocationOutcome.thrown(ex), null, msg -> null, null).onMessage(new MockTextMessage(),
                session));

        /**
         * When an executable throws, the thrown should be applied to the Post Execution
         * first. Then re-throw by the dispatcher.
         */
        Assertions.assertEquals(ex, thrown, "Should re-throw");
        Assertions.assertEquals(null, ref.get().getOutcome().getReturned(), "Should apply to Post Execution");
        Assertions.assertEquals(ex, ref.get().getOutcome().getThrown());
    }

    @Test
    void unmatched_ex_01() {
        Assertions.assertThrows(UnknownTypeException.class,
                () -> new DefaultInvokableDispatcher(msg -> null, (e, c) -> () -> null, null, msg -> null, null)
                        .onMessage(new MockTextMessage(), session));
    }

    @Test
    void failedMsg_01() throws JMSException {
        final var ref = new FailedMsg[1];
        final var msg = new MockTextMessage();
        new DefaultInvokableDispatcher(m -> null, (e, c) -> () -> null, null, m -> null, m -> {
            ref[0] = m;
        }).onMessage(msg, session);

        Assertions.assertEquals(msg.getJMSCorrelationID(), ref[0].msg().correlationId());
        Assertions.assertEquals(UnknownTypeException.class, ref[0].exception().getClass());
    }

    @Test
    void failedMsg_02() throws JMSException {
        final var ref = new FailedMsg[1];
        final var msg = new MockTextMessage();
        final var ex = new RuntimeException();
        new DefaultInvokableDispatcher(m -> new ExecutableRecord(null, null),
                (e, c) -> () -> InvocationOutcome.thrown(ex), null, m -> null, m -> {
                    ref[0] = m;
                }).onMessage(msg, session);

        Assertions.assertEquals(ex, ref[0].exception(), "should be the one thrown by application code");
    }

    @Test
    void failedMsg_03() throws JMSException {
        final var ex = new NullPointerException();

        final var t = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvokableDispatcher(m -> new ExecutableRecord(null, null),
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
                () -> new DefaultInvokableDispatcher(m -> new ExecutableRecord(null, null),
                        (e, c) -> () -> InvocationOutcome.thrown(ex), null, m -> null, m -> {
                            ref[0] = m;
                            throw new RuntimeException(m.exception());
                        }).onMessage(new MockTextMessage(), session));

        Assertions.assertEquals(t.getCause(), ex, "should be from the consumer");
        Assertions.assertEquals(t.getCause(), ref[0].exception(), "should allow the consumer to throw");
    }
}
