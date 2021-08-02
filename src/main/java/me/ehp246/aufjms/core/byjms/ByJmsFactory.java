package me.ehp246.aufjms.core.byjms;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Objects;

import javax.jms.Destination;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.DestinationResolver;
import me.ehp246.aufjms.api.jms.MsgPortDestinationSupplier;
import me.ehp246.aufjms.api.jms.MsgPortProvider;
import me.ehp246.aufjms.core.reflection.ProxyInvoked;
import me.ehp246.aufjms.core.reflection.ReflectingType;

/**
 *
 * @author Lei Yang
 *
 */
public final class ByJmsFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByJmsFactory.class);

    private final ReplyEndpointConfiguration replyConfig;
    private final MsgPortProvider portProvider;
    private final DestinationResolver nameResolver;

    public ByJmsFactory(final MsgPortProvider portProvider, final DestinationResolver nameResolver,
            final ReplyEndpointConfiguration replyConfig) {
        super();
        this.portProvider = Objects.requireNonNull(portProvider);
        this.nameResolver = Objects.requireNonNull(nameResolver);
        this.replyConfig = replyConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byJmsInterface, final ByJmsProxyConfig jmsProxyConfig) {

        final var destinatinName = byJmsInterface.getAnnotation(ByJms.class).value();
        final var port = portProvider.get(new MsgPortDestinationSupplier() {
            private final String replyTo = replyConfig.getReplyToName();

            @Override
            public Destination getTo() {
                return nameResolver.resolve(destinatinName);
            }

            @Override
            public Destination getReplyTo() {
                return replyTo == null ? null : nameResolver.resolve(replyTo);
            }

        });

        final var reflectedInterface = new ReflectingType<>(byJmsInterface);
        final var timeout = reflectedInterface.findOnType(ByJms.class).map(ByJms::timeout).filter(i -> i > 0)
                .orElseGet(replyConfig::getTimeout);
        final var ttl = reflectedInterface.findOnType(ByJms.class).map(ByJms::ttl).filter(i -> i > 0)
                .orElseGet(replyConfig::getTtl);

        LOGGER.debug("Proxying {}@{}", destinatinName, byJmsInterface.getCanonicalName());

        return (T) Proxy.newProxyInstance(byJmsInterface.getClassLoader(), new Class[] { byJmsInterface },
                (InvocationHandler) (proxy, method, args) -> {
                    if (method.getName().equals("toString")) {
                        return this.toString();
                    }
                    if (method.getName().equals("hashCode")) {
                        return this.hashCode();
                    }
                    if (method.getName().equals("equals")) {
                        return this.equals(args[0]);
                    }
                    if (method.isDefault()) {
                        return MethodHandles.privateLookupIn(byJmsInterface, MethodHandles.lookup())
                                .findSpecial(byJmsInterface, method.getName(),
                                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                        byJmsInterface)
                                .bindTo(proxy).invokeWithArguments(args);
                    }

                    final var invocation = new ByMsgInvocation(new ProxyInvoked<Object>(proxy, method, args),
                            replyConfig.getFromBody(), timeout, ttl);
                    final var correlMap = replyConfig.getCorrelMap();

                    if (invocation.isReplyExpected()) {
                        correlMap.put(invocation.getCorrelationId(), invocation);
                    }

                    try {
                        port.accept(invocation);
                    } catch (final Exception e) {
                        correlMap.remove(invocation.getCorrelationId());
                        throw e;
                    }

                    try {
                        return invocation.returnInvocation();
                    } catch (final Exception e) {
                        throw e;
                    } finally {
                        correlMap.remove(invocation.getCorrelationId());
                    }
                });

    }

    public <T> T newInstance(final Class<T> byJmsInterface) {
        final var byJms = byJmsInterface.getAnnotation(ByJms.class);

        return this.newInstance(byJmsInterface, new ByJmsProxyConfig() {

            @Override
            public long ttl() {
                return byJms.ttl();
            }

            @Override
            public String destination() {
                return byJms.value();
            }

            @Override
            public String connection() {
                return byJms.connection();
            }
        });
    }
}
