package me.ehp246.aufjms.core.dispatch;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.dispatch.ByJmsConfig;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class ByJmsProxyFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByJmsProxyFactory.class);

    private final InvocationDispatchBuilder dispatchBuilder;
    private final JmsDispatchFnProvider dispatchFnProvider;
    private final PropertyResolver propertyResolver;
    private final EnableByJmsConfig enableByJmsConfig;

    public ByJmsProxyFactory(final EnableByJmsConfig enableByJmsConfig, final JmsDispatchFnProvider dispatchFnProvider,
            final InvocationDispatchBuilder dispatchProvider, final PropertyResolver propertyResolver) {
        super();
        this.enableByJmsConfig = enableByJmsConfig;
        this.dispatchBuilder = dispatchProvider;
        this.dispatchFnProvider = dispatchFnProvider;
        this.propertyResolver = propertyResolver;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> proxyInterface) {
        LOGGER.atDebug().log("Instantiating {}", proxyInterface.getCanonicalName());

        final var byJms = proxyInterface.getAnnotation(ByJms.class);

        final var toName = propertyResolver.resolve(byJms.value().value());
        if (!OneUtil.hasValue(toName)) {
            throw new IllegalArgumentException("Un-supported To: '" + toName + "'");
        }

        final var destination = byJms.value().type() == DestinationType.QUEUE ? At.toQueue(toName) : At.toTopic(toName);

        final var replyToName = propertyResolver.resolve(byJms.replyTo().value());

        final var replyTo = OneUtil.hasValue(replyToName)
                ? byJms.replyTo().type() == DestinationType.QUEUE ? At.toQueue(replyToName) : At.toTopic(replyToName)
                : null;

        final var ttl = Duration.parse(propertyResolver.resolve(byJms.ttl().equals("")
                ? (enableByJmsConfig.ttl().equals("") ? Duration.ZERO.toString() : enableByJmsConfig.ttl())
                : byJms.ttl()));

        final var byJmsConfig = new ByJmsConfig(destination, replyTo, ttl, null,
                byJms.connectionFactory());

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

                    final var jmsDispatch = dispatchBuilder.get(proxy, method, args, byJmsConfig);

                    dispatchFn.send(jmsDispatch);

                    return null;
                });

    }
}
