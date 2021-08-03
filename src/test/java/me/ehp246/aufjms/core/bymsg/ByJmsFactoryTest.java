package me.ehp246.aufjms.core.bymsg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.jms.DispatchFn;
import me.ehp246.aufjms.api.jms.DispatchFnProvider;
import me.ehp246.aufjms.api.jms.InvocationDispatchProvider;
import me.ehp246.aufjms.core.byjms.ByJmsFactory;

class ByJmsFactoryTest {
    private final DispatchFn dispatchFn = dispatch -> null;
    private final InvocationDispatchProvider dispatchProvider = inovcation -> null;
    private final DispatchFnProvider dispatchFnProvider = connection -> dispatchFn;

    private final ByJmsFactory factory = new ByJmsFactory(dispatchProvider, dispatchFnProvider);

    @Test
    void object_01() {
        final var newInstance = factory.newInstance(TestCases.Case01.class);

        Assertions.assertTrue(newInstance instanceof TestCases.Case01);
        Assertions.assertTrue(newInstance.toString() instanceof String);
        Assertions.assertTrue(newInstance.hashCode() > 0);
        Assertions.assertTrue(newInstance.equals(newInstance));
        Assertions.assertTrue(!newInstance.equals(null));
    }

    @Test
    void default_01() {
        Assertions.assertEquals(124, factory.newInstance(TestCases.Case01.class).inc(123));
    }
}
