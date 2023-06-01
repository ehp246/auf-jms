package me.ehp246.aufjms.core.dispatch;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.exception.JmsDispatchException;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.dispatch.ByJmsProxyFactoryTestCases.FutureMapCase01;
import me.ehp246.test.Jackson;
import me.ehp246.test.mock.MockDispatch;

class ByJmsProxyFactoryTest {
    private final JmsDispatchFn dispatchFn = dispatch -> null;
    private final JmsDispatchFnProvider dispatchFnProvider = name -> dispatchFn;
    private final PropertyResolver propertyResolver = Object::toString;
    private final EnableByJmsConfig localReturnConfig = new EnableByJmsConfig();
    private final EnableByJmsConfig remoteReturnConfig = new EnableByJmsConfig(List.of(), null, null, List.of(),
            At.toQueue("mock"));
    private final DispatchMethodParser methodParser = new DefaultDispatchMethodParser(propertyResolver,
            Jackson.jsonService());
    private final ReplyExpectedDispatchMap dispatchMap = new DefaultReplyExpectedDispatchMap();

    private final ByJmsProxyFactory factory = new ByJmsProxyFactory(localReturnConfig, dispatchFnProvider,
            propertyResolver, methodParser, dispatchMap);

    @Test
    void object_01() {
        final var newInstance = factory.newByJmsProxy(ByJmsProxyFactoryTestCases.Case01.class);

        Assertions.assertTrue(newInstance instanceof ByJmsProxyFactoryTestCases.Case01);
        Assertions.assertTrue(newInstance.toString() instanceof String);
        Assertions.assertTrue(newInstance.hashCode() > 0);
        Assertions.assertTrue(newInstance.equals(newInstance));
        Assertions.assertTrue(!newInstance.equals(null));
    }

    @Test
    void default_01() {
        Assertions.assertEquals(124, factory.newByJmsProxy(ByJmsProxyFactoryTestCases.Case01.class).inc(123));
    }

    @Test
    void property_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                factory.newByJmsProxy(ByJmsProxyFactoryTestCases.PropertyCase01.class)::ping);
    }

    @Test
    void connection_01() {
        final var disp = new JmsDispatch[1];
        final var dispatchFn = new JmsDispatchFn() {

            @Override
            public JmsMsg send(final JmsDispatch dispatch) {
                disp[0] = dispatch;
                return null;
            }
        };
        final var con = new String[1];
        new ByJmsProxyFactory(localReturnConfig, conection -> {
            con[0] = conection;
            return dispatchFn;
        }, propertyResolver, methodParser, dispatchMap).newByJmsProxy(ByJmsProxyFactoryTestCases.Case01.class);

        Assertions.assertEquals("SB1", con[0], "should ask for the Fn by the connection name");
    }

    @Test
    void futureMap_01() {
        final var dispatchMap = new DefaultReplyExpectedDispatchMap();
        final var factory = new ByJmsProxyFactory(remoteReturnConfig, dispatchFnProvider, propertyResolver,
                methodParser, dispatchMap);

        final var instance = factory.newByJmsProxy(FutureMapCase01.class);

        final var thrown = Assertions.assertThrows(JmsDispatchException.class, () -> instance.get());

        Assertions.assertEquals(TimeoutException.class, thrown.getCause().getClass());

        Assertions.assertEquals(0, dispatchMap.getMap().size(), "should be removed");
    }

    @Test
    void futureMap_02() {
        final var dispatchMap = new DefaultReplyExpectedDispatchMap();
        final var factory = new ByJmsProxyFactory(remoteReturnConfig, dispatchFnProvider, propertyResolver,
                (method, config) -> new DispatchMethodBinder((proxy, args) -> new MockDispatch(),
                        (RemoteReturnBinder) ((dispatch, future) -> 0)),
                dispatchMap);

        final var instance = factory.newByJmsProxy(FutureMapCase01.class);

        Assertions.assertEquals(0, instance.get());

        Assertions.assertEquals(0, dispatchMap.getMap().size(), "should be removed");
    }

    @Test
    void futureMap_03() {
        final var dispatchMap = new DefaultReplyExpectedDispatchMap();
        final var factory = new ByJmsProxyFactory(remoteReturnConfig, dispatchFnProvider, propertyResolver,
                (method, config) -> new DispatchMethodBinder((proxy, args) -> new MockDispatch(),
                        (RemoteReturnBinder) ((dispatch, future) -> {
                            throw new RuntimeException();
                        })),
                dispatchMap);

        final var instance = factory.newByJmsProxy(FutureMapCase01.class);

        Assertions.assertThrows(RuntimeException.class, () -> instance.get());

        Assertions.assertEquals(0, dispatchMap.getMap().size(), "should be removed");
    }
}
