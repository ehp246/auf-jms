package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;
import me.ehp246.aufjms.util.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class DefaultInvokableDispatcherTest {

    @Test
    void ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvokableDispatcher(jmsMsg -> {
                    return new Executable() {

                        @Override
                        public Method getMethod() {
                            // TODO Auto-generated method stub
                            return null;
                        }

                        @Override
                        public Object getInstance() {
                            // TODO Auto-generated method stub
                            return null;
                        }
                    };
                }, (e, c) -> {
                    return () -> {
                        return InvocationOutcome.thrown(ex);
                    };
                }, null, msg -> null, null).dispatch(new MockJmsMsg()));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void resolver_ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvokableDispatcher(jmsMsg -> {
                    throw ex;
                }, (e, c) -> () -> InvocationOutcome.returned(null), null, msg -> null, null)
                        .dispatch(new MockJmsMsg()));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void postexecution_01() {
        final var ref = new AtomicReference<ExecutedInstance>();
        new DefaultInvokableDispatcher(msg -> {
            return new Executable() {

                @Override
                public Method getMethod() {
                    return null;
                }

                @Override
                public Object getInstance() {
                    return null;
                }

                @Override
                public Consumer<ExecutedInstance> executionConsumer() {
                    return ei -> ref.set(ei);
                }
            };
        }, (e, c) -> () -> InvocationOutcome.returned(ref), null, msg -> null, null).dispatch(new MockJmsMsg());

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
                public Method getMethod() {
                    return null;
                }

                @Override
                public Object getInstance() {
                    return null;
                }

                @Override
                public Consumer<ExecutedInstance> executionConsumer() {
                    return ei -> ref.set(ei);
                }
            };
        }, (e, c) -> () -> InvocationOutcome.thrown(ex), null, msg -> null, null).dispatch(new MockJmsMsg()));

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
        Assertions.assertThrows(UnknownTypeException.class, () -> new DefaultInvokableDispatcher(msg -> null,
                (e, c) -> () -> null, null, msg -> null, null).dispatch(new MockJmsMsg()));
    }
}
