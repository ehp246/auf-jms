package me.ehp246.aufjms.core.byjms;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.jms.Destination;

import me.ehp246.aufjms.api.Invocation;
import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.DestinationResolver;
import me.ehp246.aufjms.api.jms.InvocationDispatchProvider;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.core.reflection.ProxyInvocation;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class JmsDispatchFromInvocation implements InvocationDispatchProvider {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of();

    private final ByJmsProxyConfig proxyConfig;
    private final DestinationResolver destinationResolver;

    public JmsDispatchFromInvocation(final ByJmsProxyConfig proxyConfig,
            final DestinationResolver destinationResolver) {
        super();
        this.proxyConfig = proxyConfig;
        this.destinationResolver = destinationResolver;
    }

    @Override
    public JmsDispatch get(final Invocation invocation) {
        final var proxyInvocation = new ProxyInvocation(invocation.method().getDeclaringClass(), invocation.target(),
                invocation.method(),
                invocation.args());
        final var destination = this.destinationResolver.resolve(this.proxyConfig.connection(),
                this.proxyConfig.destination());
        final var replyTo = Optional.ofNullable(this.proxyConfig.replyTo()).filter(OneUtil::hasValue)
                .map(name -> this.destinationResolver.resolve(this.proxyConfig.connection(), name)).orElse(null);
        final var type = proxyInvocation.getMethodName().substring(0, 1).toUpperCase(Locale.US)
                + proxyInvocation.getMethodName().substring(1);
        final var correlId = UUID.randomUUID().toString();
        final var ttl = proxyConfig.ttl();
        final var bodyValues = Collections.unmodifiableList(proxyInvocation.filterPayloadArgs(PARAMETER_ANNOTATIONS));

        return new JmsDispatch() {

            @Override
            public Destination destination() {
                return destination;
            }

            @Override
            public String type() {
                return type;
            }

            @Override
            public String correlationId() {
                return correlId;
            }

            @Override
            public List<?> bodyValues() {
                return bodyValues;
            }

            @Override
            public Destination replyTo() {
                return replyTo;
            }

            @Override
            public Long ttl() {
                return ttl;
            }

            @Override
            public String groupId() {
                return JmsDispatch.super.groupId();
            }

            @Override
            public Integer groupSeq() {
                return JmsDispatch.super.groupSeq();
            }

        };
    }
}
