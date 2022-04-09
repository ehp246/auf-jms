package me.ehp246.aufjms.core.dispatch;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.dispatch.InvocationDispatchConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.Invocation;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class ByJmsFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByJmsFactory.class);

    private final InvocationDispatchBuilder dispatchProvider;
    private final JmsDispatchFnProvider dispatchFnProvider;

    public ByJmsFactory(final JmsDispatchFnProvider dispatchFnProvider,
            final InvocationDispatchBuilder dispatchProvider) {
        super();
        this.dispatchProvider = dispatchProvider;
        this.dispatchFnProvider = dispatchFnProvider;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byJmsInterface, final InvocationDispatchConfig jmsDispatchConfig,
            final String connectionFactoryName) {
        LOGGER.atDebug().log("Instantiating {}", byJmsInterface.getCanonicalName());

        final JmsDispatchFn dispatchFn = this.dispatchFnProvider.get(connectionFactoryName);
        final var hashCode = new Object().hashCode();
        return (T) Proxy.newProxyInstance(byJmsInterface.getClassLoader(), new Class[] { byJmsInterface },
                (InvocationHandler) (proxy, method, args) -> {
                    if (method.getName().equals("toString")) {
                        return this.toString();
                    }
                    if (method.getName().equals("hashCode")) {
                        return hashCode;
                    }
                    if (method.getName().equals("equals")) {
                        return proxy == args[0];
                    }
                    if (method.isDefault()) {
                        return MethodHandles.privateLookupIn(byJmsInterface, MethodHandles.lookup())
                                .findSpecial(byJmsInterface, method.getName(),
                                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                        byJmsInterface)
                                .bindTo(proxy).invokeWithArguments(args);
                    }

                    final var jmsDispatch = dispatchProvider.get(new Invocation() {
                        private final List<?> asList = Collections
                                .unmodifiableList(args == null ? List.of() : Arrays.asList(args));

                        @Override
                        public Object target() {
                            return proxy;
                        }

                        @Override
                        public Method method() {
                            return method;
                        }

                        @Override
                        public List<?> args() {
                            return asList;
                        }
                    }, jmsDispatchConfig);

                    dispatchFn.send(jmsDispatch);

                    return null;
                });

    }
}
