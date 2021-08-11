package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.jms.Destination;

import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.DestinationProvider;
import me.ehp246.aufjms.api.jms.Invocation;
import me.ehp246.aufjms.core.reflection.DefaultProxyInvocation;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocationDispatchBuilder implements InvocationDispatchBuilder {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of();
    private final DestinationProvider destinationResolver;

    public DefaultInvocationDispatchBuilder(final DestinationProvider destinationResolver) {
        super();
        this.destinationResolver = destinationResolver;
    }

    @Override
    public JmsDispatch get(final ByJmsProxyConfig config, final Invocation invocation) {
        final var proxyInvocation = new DefaultProxyInvocation(invocation.method().getDeclaringClass(),
                invocation.target(), invocation.method(), invocation.args());

        // Destination is required.
        final var destination = this.destinationResolver.get(config.connection(), config.destination());

        // In the priority of Argument, Method, Type.
        final String type = proxyInvocation.resolveAnnotatedValue(OfType.class,
                arg -> arg.argument() != null ? arg.argument().toString()
                        : arg.annotation().value().isBlank() ? null : arg.annotation().value(),
                ofType -> ofType.value().isBlank() ? OneUtil.firstUpper(proxyInvocation.getMethodName())
                        : ofType.value(),
                ofType -> ofType.value().isBlank() ? proxyInvocation.getDeclaringClassSimpleName() : ofType.value(),
                proxyInvocation::getDeclaringClassSimpleName);

        final Duration ttl = proxyInvocation.methodAnnotationOf(OfTtl.class,
                anno -> anno.value().equals("") ? config.ttl() : Duration.parse(anno.value()),
                config::ttl);
        // ReplyTo is optional.
        final var replyTo = Optional.ofNullable(config.replyTo()).filter(OneUtil::hasValue)
                .map(name -> this.destinationResolver.get(config.connection(), name)).orElse(null);
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
                return ttl;
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
