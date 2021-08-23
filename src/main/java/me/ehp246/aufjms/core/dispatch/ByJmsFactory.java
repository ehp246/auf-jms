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

import me.ehp246.aufjms.api.dispatch.DispatchConfig;
import me.ehp246.aufjms.api.dispatch.DispatchFn;
import me.ehp246.aufjms.api.dispatch.DispatchFnProvider;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.jms.Invocation;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class ByJmsFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByJmsFactory.class);

    private final InvocationDispatchBuilder dispatchProvider;
    private final DispatchFnProvider dispatchFnProvider;

    public ByJmsFactory(final DispatchFnProvider dispatchFnProvider,
            final InvocationDispatchBuilder dispatchProvider) {
        super();
        this.dispatchProvider = dispatchProvider;
        this.dispatchFnProvider = dispatchFnProvider;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byJmsInterface, final DispatchConfig jmsProxyConfig) {
        LOGGER.atDebug().log("Instantiating {}", byJmsInterface.getCanonicalName());

        final DispatchFn dispatchFn = this.dispatchFnProvider.get(jmsProxyConfig.context());
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
                    }, jmsProxyConfig);

                    dispatchFn.dispatch(jmsDispatch);

                    return null;
                });

    }
}
