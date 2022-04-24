package me.ehp246.aufjms.core.bymsg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.reflection.Invocation;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.dispatch.ByJmsFactory;

class ByJmsFactoryTest {
    private final JmsDispatchFn dispatchFn = dispatch -> null;
    private final InvocationDispatchBuilder dispatchProvider = (invocation, config) -> null;
    private final JmsDispatchFnProvider dispatchFnProvider = connection -> dispatchFn;
    private final PropertyResolver propertyResolver = n -> n;
    private final EnableByJmsConfig config = new EnableByJmsConfig();

    private final ByJmsFactory factory = new ByJmsFactory(config, dispatchFnProvider, dispatchProvider,
            propertyResolver);

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

    @Test
    void connection_01() {
        final var jmsDispatch = (JmsDispatch) () -> null;

        final var disp = new JmsDispatch[1];
        final var dispatchFn = new JmsDispatchFn() {

            @Override
            public JmsMsg send(JmsDispatch dispatch) {
                disp[0] = dispatch;
                return null;
            }
        };
        final var con = new String[1];
        final var inv = new Invocation[1];
        final var newInstance = new ByJmsFactory(config, conection -> {
            con[0] = conection;
            return dispatchFn;
        }, (invocation, config) -> {
            inv[0] = invocation;
            return jmsDispatch;
        }, propertyResolver).newInstance(TestCases.Case01.class);

        Assertions.assertEquals("SB1", con[0], "should ask for the Fn by the connection name");

        newInstance.m001();

        Assertions.assertEquals(newInstance, inv[0].target());
        Assertions.assertEquals(TestCases.Case01.class, inv[0].method().getDeclaringClass());

        Assertions.assertEquals(jmsDispatch, disp[0], "should pass JmsDispatch to DispatchFn");
    }
}
