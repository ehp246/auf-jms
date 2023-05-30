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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFnProvider;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 * @since 1.0
 * @see EnableByJmsRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata,
 *      org.springframework.beans.factory.support.BeanDefinitionRegistry)
 */
public final class ByJmsProxyFactory {
    private final static Logger LOGGER = LogManager.getLogger();

    private final Map<Method, DispatchMethodBinder> methodBinderCache = new ConcurrentHashMap<>();

    private final JmsDispatchFnProvider dispatchFnProvider;
    private final PropertyResolver propertyResolver;
    private final EnableByJmsConfig enableByJmsConfig;
    private final ReplyExpectedDispatchMap replyExpectedDispatchMap;
    private final DispatchMethodParser methodParser;

    public ByJmsProxyFactory(final EnableByJmsConfig enableByJmsConfig, final JmsDispatchFnProvider dispatchFnProvider,
            final PropertyResolver propertyResolver, final DispatchMethodParser methodParser,
            @Nullable final ReplyExpectedDispatchMap replyExpectedDispatchMap) {
        super();
        this.enableByJmsConfig = enableByJmsConfig;
        this.dispatchFnProvider = dispatchFnProvider;
        this.propertyResolver = propertyResolver;
        this.replyExpectedDispatchMap = replyExpectedDispatchMap;
        this.methodParser = methodParser;
    }

    @SuppressWarnings("unchecked")
    public <T> T newByJmsProxy(final Class<T> proxyInterface) {
        LOGGER.atDebug().log("Instantiating {}", proxyInterface::getCanonicalName);

        final var byJms = proxyInterface.getAnnotation(ByJms.class);

        final var toName = propertyResolver.resolve(byJms.value().value());
        if (!OneUtil.hasValue(toName)) {
            throw new IllegalArgumentException("Un-supported To: '" + toName + "'");
        }

        final var destination = byJms.value().type() == DestinationType.QUEUE ? At.toQueue(toName) : At.toTopic(toName);

        final var replyToName = propertyResolver.resolve(byJms.replyTo().value());
        final var replyTo = OneUtil.hasValue(replyToName)
                ? byJms.replyTo().type() == DestinationType.QUEUE ? At.toQueue(replyToName) : At.toTopic(replyToName)
                : enableByJmsConfig.requestReplyAt();

        final var dispatchReplyTimeout = OneUtil.hasValue(byJms.requstTimeout())
                ? Duration.parse(propertyResolver.resolve(byJms.requstTimeout()))
                : null;

        final var ttl = Optional.of(propertyResolver.resolve(byJms.ttl())).filter(OneUtil::hasValue)
                .map(Duration::parse).orElseGet(enableByJmsConfig::ttl);

        final var delay = Optional.of(propertyResolver.resolve(byJms.delay())).filter(OneUtil::hasValue)
                .map(Duration::parse).orElseGet(enableByJmsConfig::delay);

        return (T) Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[] { proxyInterface },
                new InvocationHandler() {
                    private final ByJmsProxyConfig proxyConfig = new ByJmsProxyConfig(destination, replyTo,
                            dispatchReplyTimeout, ttl, delay, byJms.connectionFactory(), List.of(byJms.properties()));
                    private final JmsDispatchFn dispatchFn = dispatchFnProvider.get(byJms.connectionFactory());
                    private final int hashCode = new Object().hashCode();

                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args)
                            throws Throwable {
                        if (method.getName().equals("toString")) {
                            return ByJmsProxyFactory.this.toString();
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

                        final var methodBinder = methodBinderCache.computeIfAbsent(method,
                                m -> methodParser.parse(m, proxyConfig));

                        final var jmsDispatch = methodBinder.invocationBinder().apply(proxy, args);

                        final var returnBinder = methodBinder.returnBinder();

                        // Return msg expected?
                        final CompletableFuture<JmsMsg> futureMsg = (returnBinder instanceof RemoteReturnBinder)
                                ? replyExpectedDispatchMap.put(jmsDispatch.correlationId())
                                : null;

                        dispatchFn.send(jmsDispatch);

                        if (returnBinder instanceof LocalReturnBinder localBinder) {
                            return localBinder.apply(jmsDispatch);
                        }

                        return ((RemoteReturnBinder) returnBinder).apply(jmsDispatch, futureMsg);
                    }
                });
    }
}
