package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.endpoint.Executable;
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
        }, null).dispatch(new MockJmsMsg()));

        Assertions.assertEquals(true, ex == thrown);
    }

    @Test
    void resolver_ex_01() {
        final var ex = new RuntimeException();
        final var thrown = Assertions.assertThrows(RuntimeException.class,
                () -> new DefaultInvokableDispatcher(jmsMsg -> {
                    throw ex;
                }, (e, c) -> () -> InvocationOutcome.returned(null), null).dispatch(new MockJmsMsg()));

        Assertions.assertEquals(true, ex == thrown);
    }
}
