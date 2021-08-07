package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.jms.Destination;

import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.DestinationNameResolver;
import me.ehp246.aufjms.api.jms.Invocation;
import me.ehp246.aufjms.core.reflection.AnnotatedArgument;
import me.ehp246.aufjms.core.reflection.DefaultProxyInvocation;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocationDispatchBuilder implements InvocationDispatchBuilder {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of();
    private final DestinationNameResolver destinationResolver;

    public DefaultInvocationDispatchBuilder(final DestinationNameResolver destinationResolver) {
        super();
        this.destinationResolver = destinationResolver;
    }

    @Override
    public JmsDispatch get(final ByJmsProxyConfig config, final Invocation invocation) {
        final var proxyInvocation = new DefaultProxyInvocation(invocation.method().getDeclaringClass(), invocation.target(),
                invocation.method(), invocation.args());

        // Destination is required.
        final var destination = this.destinationResolver.resolve(config.connection(), config.destination());

        final var type = proxyInvocation.streamOfAnnotatedArguments(OfType.class).findFirst()
                .map(AnnotatedArgument::argument)
                .map(OneUtil::toString)
                .filter(OneUtil::hasValue)
                .orElseGet(
                        () -> proxyInvocation.findOnMethodUp(OfType.class).map(OfType::value).filter(OneUtil::hasValue)
                                .orElseGet(() -> proxyInvocation.getMethodName().substring(0, 1).toUpperCase(Locale.US)
                                        + proxyInvocation.getMethodName().substring(1)));

        // ReplyTo is optional.
        final var replyTo = Optional.ofNullable(config.replyTo()).filter(OneUtil::hasValue)
                .map(name -> this.destinationResolver.resolve(config.connection(), name)).orElse(null);
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
