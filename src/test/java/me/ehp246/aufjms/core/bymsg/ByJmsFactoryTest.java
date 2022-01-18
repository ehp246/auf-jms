package me.ehp246.aufjms.core.bymsg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.DispatchConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.Invocation;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.dispatch.ByJmsFactory;

class ByJmsFactoryTest {
    private final JmsDispatchFn dispatchFn = dispatch -> null;
    private final InvocationDispatchBuilder dispatchProvider = (invocation, config) -> null;
    private final JmsDispatchFnProvider dispatchFnProvider = connection -> dispatchFn;

    private final ByJmsFactory factory = new ByJmsFactory(dispatchFnProvider, dispatchProvider);

    private final static AtDestination defaultAt = new AtDestination() {

        @Override
        public DestinationType type() {
            return DestinationType.QUEUE;
        }

        @Override
        public String name() {
            return "";
        }
    };

    private DispatchConfig case01() {
        return new DispatchConfig() {

            @Override
            public String ttl() {
                return "PT10S";
            }

            @Override
            public AtDestination replyTo() {
                return defaultAt;
            }

            @Override
            public AtDestination destination() {
                return new AtDestination() {

                    @Override
                    public DestinationType type() {
                        return DestinationType.QUEUE;
                    }

                    @Override
                    public String name() {
                        return "queue1";
                    }
                };
            }
        };
    }

    @Test
    void object_01() {
        final var newInstance = factory.newInstance(TestCases.Case01.class, case01(), null);

        Assertions.assertTrue(newInstance instanceof TestCases.Case01);
        Assertions.assertTrue(newInstance.toString() instanceof String);
        Assertions.assertTrue(newInstance.hashCode() > 0);
        Assertions.assertTrue(newInstance.equals(newInstance));
        Assertions.assertTrue(!newInstance.equals(null));
    }

    @Test
    void default_01() {
        Assertions.assertEquals(124, factory.newInstance(TestCases.Case01.class, case01(), null).inc(123));
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
        final var newInstance = new ByJmsFactory(conection -> {
            con[0] = conection;
            return dispatchFn;
        }, (invocation, config) -> {
            inv[0] = invocation;
            return jmsDispatch;
        }).newInstance(TestCases.Case01.class, case01(), "SB1");

        Assertions.assertEquals("SB1", con[0], "should ask for the Fn by the connection name");

        newInstance.m001();

        Assertions.assertEquals(newInstance, inv[0].target());
        Assertions.assertEquals(TestCases.Case01.class, inv[0].method().getDeclaringClass());

        Assertions.assertEquals(jmsDispatch, disp[0], "should pass JmsDispatch to DispatchFn");
    }
}
