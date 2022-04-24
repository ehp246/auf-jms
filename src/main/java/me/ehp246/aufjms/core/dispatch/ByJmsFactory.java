package me.ehp246.aufjms.core.dispatch;

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
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.reflection.Invocation;
import me.ehp246.aufjms.api.spi.PropertyResolver;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class ByJmsFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByJmsFactory.class);

    private final InvocationDispatchBuilder dispatchProvider;
    private final JmsDispatchFnProvider dispatchFnProvider;
    private final PropertyResolver propertyResolver;

    public ByJmsFactory(final JmsDispatchFnProvider dispatchFnProvider,
            final InvocationDispatchBuilder dispatchProvider, final PropertyResolver propertyResolver) {
        super();
        this.dispatchProvider = dispatchProvider;
        this.dispatchFnProvider = dispatchFnProvider;
        this.propertyResolver = propertyResolver;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(EnableByJmsConfig enableByJmsConfig, final Class<T> proxyInterface) {
        LOGGER.atDebug().log("Instantiating {}", proxyInterface.getCanonicalName());

        final var byJms = proxyInterface.getAnnotation(ByJms.class);

        final var toName = propertyResolver.resolve(byJms.value().value());
        final var destination = byJms.value().type() == DestinationType.QUEUE ? At.toQueue(toName)
                : At.toTopic(toName);

        final var replyToName = propertyResolver.resolve(byJms.replyTo().value());
        final var replyTo = byJms.replyTo().type() == DestinationType.QUEUE ? At.toQueue(replyToName)
                : At.toTopic(replyToName);

        final var ttl = propertyResolver.resolve(byJms.ttl().equals("")
                ? (enableByJmsConfig.ttl().equals("") ? Duration.ZERO.toString() : enableByJmsConfig.ttl())
                : byJms.ttl());

        final var jmsDispatchConfig = new InvocationDispatchConfig() {
            @Override
            public String ttl() {
                return ttl;
            }

            @Override
            public At to() {
                return destination;
            }

            @Override
            public At replyTo() {
                return replyTo;
            }
        };

        final JmsDispatchFn dispatchFn = this.dispatchFnProvider.get(byJms.connectionFactory());
        final var hashCode = new Object().hashCode();

        return (T) Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[] { proxyInterface },
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
                        return MethodHandles.privateLookupIn(proxyInterface, MethodHandles.lookup())
                                .findSpecial(proxyInterface, method.getName(),
                                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                        proxyInterface)
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
