package me.ehp246.aufjms.core.dispatch;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 * @since 1.0
 * @see EnableByJmsRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata,
 *      org.springframework.beans.factory.support.BeanDefinitionRegistry)
 */
public final class ByJmsProxyFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(ByJmsProxyFactory.class);

    private final Map<Method, DispatchMethodBinder> methodBinderCache = new ConcurrentHashMap<>();

    private final JmsDispatchFnProvider dispatchFnProvider;
    private final PropertyResolver propertyResolver;
    private final EnableByJmsConfig enableByJmsConfig;
    private final RequestDispatchMap requestDispatchMap;
    private final DispatchMethodParser methodParser;

    public ByJmsProxyFactory(final EnableByJmsConfig enableByJmsConfig,
            final JmsDispatchFnProvider dispatchFnProvider, final PropertyResolver propertyResolver,
            final DispatchMethodParser methodParser,
            @Nullable final RequestDispatchMap requestDispatchMap) {
        super();
        this.enableByJmsConfig = enableByJmsConfig;
        this.dispatchFnProvider = dispatchFnProvider;
        this.propertyResolver = propertyResolver;
        this.requestDispatchMap = requestDispatchMap;
        this.methodParser = methodParser;
    }

    @SuppressWarnings("unchecked")
    public <T> T newByJmsProxy(final Class<T> proxyInterface) {
        LOGGER.atDebug().setMessage("Instantiating {}")
                .addArgument(proxyInterface::getCanonicalName).log();

        final var byJms = proxyInterface.getAnnotation(ByJms.class);

        final var toName = propertyResolver.resolve(byJms.value().value());
        if (!OneUtil.hasValue(toName)) {
            throw new IllegalArgumentException("Un-supported To: '" + toName + "'");
        }

        final var destination = byJms.value().type() == DestinationType.QUEUE ? At.toQueue(toName)
                : At.toTopic(toName);

        final var replyToName = propertyResolver.resolve(byJms.replyTo().value());
        final var replyTo = OneUtil.hasValue(replyToName)
                ? byJms.replyTo().type() == DestinationType.QUEUE ? At.toQueue(replyToName)
                        : At.toTopic(replyToName)
                : enableByJmsConfig.requestReplyAt();

        final var requestTimeout = Optional
                .ofNullable(propertyResolver.resolve(byJms.requestTimeout()))
                .filter(OneUtil::hasValue).map(Duration::parse)
                .orElseGet(() -> Optional
                        .ofNullable(propertyResolver
                                .resolve("${" + AufJmsConstants.REQUEST_TIMEOUT + ":}"))
                        .filter(OneUtil::hasValue).map(Duration::parse).orElse(null));

        final var ttl = Optional.of(propertyResolver.resolve(byJms.ttl())).filter(OneUtil::hasValue)
                .map(Duration::parse).orElseGet(enableByJmsConfig::ttl);

        final var delay = Optional.of(propertyResolver.resolve(byJms.delay()))
                .filter(OneUtil::hasValue).map(Duration::parse).orElseGet(enableByJmsConfig::delay);

        final var proxyConfig = new ByJmsProxyConfig(destination, replyTo, requestTimeout, ttl,
                delay, byJms.connectionFactory(), List.of(byJms.properties()));

        final var dispatchFn = dispatchFnProvider.get(byJms.connectionFactory());

        final int hashCode = new Object().hashCode();

        return (T) Proxy.newProxyInstance(proxyInterface.getClassLoader(),
                new Class[] { proxyInterface }, (InvocationHandler) (proxy, method, args) -> {
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
                                        MethodType.methodType(method.getReturnType(),
                                                method.getParameterTypes()),
                                        proxyInterface)
                                .bindTo(proxy).invokeWithArguments(args);
                    }

                    final var methodBinder = methodBinderCache.computeIfAbsent(method,
                            m -> methodParser.parse(m, proxyConfig));

                    final var jmsDispatch = methodBinder.invocationBinder().apply(proxy, args);

                    final var returnBinder = methodBinder.returnBinder();

                    Optional.ofNullable(jmsDispatch.log4jContext()).orElseGet(Map::of).entrySet()
                            .stream().forEach(e -> MDC.put(e.getKey(), e.getValue()));
                    try {
                        // Reply msg expected?
                        final CompletableFuture<JmsMsg> futureMsg = (returnBinder instanceof RemoteReturnBinder)
                                ? requestDispatchMap.add(jmsDispatch.correlationId())
                                : null;

                        dispatchFn.send(jmsDispatch);

                        if (futureMsg == null) {
                            return ((LocalReturnBinder) returnBinder).apply(jmsDispatch);
                        }

                        try {
                            return ((RemoteReturnBinder) returnBinder).apply(jmsDispatch,
                                    futureMsg);
                        } finally {
                            requestDispatchMap.remove(jmsDispatch.correlationId());
                        }
                    } finally {
                        Optional.ofNullable(jmsDispatch.log4jContext()).orElseGet(Map::of)
                                .entrySet().stream().forEach(e -> MDC.remove(e.getKey()));
                    }
                });
    }
}
