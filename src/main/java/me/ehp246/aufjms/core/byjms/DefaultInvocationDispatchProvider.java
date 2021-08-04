package me.ehp246.aufjms.core.byjms;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.jms.Destination;

import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.DestinationResolver;
import me.ehp246.aufjms.api.jms.Invocation;
import me.ehp246.aufjms.api.jms.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.core.reflection.ProxyInvocation;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocationDispatchProvider implements InvocationDispatchBuilder {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of();
    private final DestinationResolver destinationResolver;

    public DefaultInvocationDispatchProvider(final DestinationResolver destinationResolver) {
        super();
        this.destinationResolver = destinationResolver;
    }

    @Override
    public JmsDispatch get(final ByJmsProxyConfig config, final Invocation invocation) {
        // Destination is required.
        final var destination = this.destinationResolver.resolve(config.connection(), config.destination());
        // ReplyTo is optional.
        final var replyTo = Optional.ofNullable(config.replyTo()).filter(OneUtil::hasValue)
                .map(name -> this.destinationResolver.resolve(config.connection(), name)).orElse(null);
        final var proxyInvocation = new ProxyInvocation(invocation.method().getDeclaringClass(), invocation.target(),
                invocation.method(), invocation.args());
        final var type = proxyInvocation.getMethodName().substring(0, 1).toUpperCase(Locale.US)
                + proxyInvocation.getMethodName().substring(1);
        final var correlId = UUID.randomUUID().toString();
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
            public Duration ttl() {
                return config.ttl();
            }

            @Override
            public String groupId() {
                return null;
            }

            @Override
            public Integer groupSeq() {
                return null;
            }

        };
    }
}
