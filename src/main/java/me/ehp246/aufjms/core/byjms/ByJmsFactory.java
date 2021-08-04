package me.ehp246.aufjms.core.byjms;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.DispatchFn;
import me.ehp246.aufjms.api.jms.Invocation;
import me.ehp246.aufjms.api.jms.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.jms.JmsDispatchFnProvider;

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
    public <T> T newInstance(final Class<T> byJmsInterface, final ByJmsProxyConfig jmsProxyConfig) {
        LOGGER.atDebug().log("Instantiating {}", byJmsInterface.getCanonicalName());

        final DispatchFn dispatchFn = this.dispatchFnProvider.get(jmsProxyConfig.connection());
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

                    final var jmsDispatch = dispatchProvider.get(jmsProxyConfig, new Invocation() {
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
                    });

                    dispatchFn.dispatch(jmsDispatch);

                    return null;
                });

    }

    public <T> T newInstance(final Class<T> byJmsInterface) {
        final var byJms = byJmsInterface.getAnnotation(ByJms.class);

        final var ttl = Duration.parse(byJms.ttl());

        return this.newInstance(byJmsInterface, new ByJmsProxyConfig() {

            @Override
            public Duration ttl() {
                return ttl;
            }

            @Override
            public String destination() {
                return byJms.destination();
            }

            @Override
            public String connection() {
                return byJms.connection();
            }

            @Override
            public String replyTo() {
                return byJms.replyTo();
            }
        });
    }
}
