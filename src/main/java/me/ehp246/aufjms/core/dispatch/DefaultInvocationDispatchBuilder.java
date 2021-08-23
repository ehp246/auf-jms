package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.DispatchConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.api.jms.Invocation;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.jms.AtDestinationRecord;
import me.ehp246.aufjms.core.reflection.DefaultProxyInvocation;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocationDispatchBuilder implements InvocationDispatchBuilder {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfType.class,
            OfProperty.class, OfTtl.class, OfDelay.class);

    private final PropertyResolver propertyResolver;

    public DefaultInvocationDispatchBuilder(final PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    @Override
    public JmsDispatch get(final Invocation invocation, final DispatchConfig config) {
        final var proxyInvocation = new DefaultProxyInvocation(invocation.method().getDeclaringClass(),
                invocation.target(), invocation.method(), invocation.args());

        // Destination is required.
        final var destination = new AtDestinationRecord(propertyResolver.resolve(config.destination().name()),
                config.destination().type());

        // Optional.
        final var replyTo = Optional.ofNullable(config.replyTo())
                .map(at -> new AtDestinationRecord(propertyResolver.resolve(at.name()), at.type())).orElse(null);

        // In the priority of Argument, Method, Type.
        final String type = proxyInvocation.resolveAnnotatedValue(OfType.class,
                arg -> arg.argument() != null ? arg.argument().toString()
                        : arg.annotation().value().isBlank() ? null : arg.annotation().value(),
                ofType -> ofType.value().isBlank() ? OneUtil.firstUpper(proxyInvocation.getMethodName())
                        : ofType.value(),
                ofType -> ofType.value().isBlank() ? proxyInvocation.getDeclaringClassSimpleName() : ofType.value(),
                () -> OneUtil.firstUpper(proxyInvocation.getMethodName()));

        final var properties = proxyInvocation.mapAnnotatedArguments(OfProperty.class, OfProperty::value);
        if (properties.get("") != null) {

        }

        // Accepts null.
        final Duration ttl = Optional
                .ofNullable(proxyInvocation.methodAnnotationOf(OfTtl.class,
                        anno -> anno.value().equals("") ? config.ttl() : anno.value(), config::ttl))
                .map(propertyResolver::resolve).filter(OneUtil::hasValue).map(Duration::parse).orElse(null);

        final var correlId = UUID.randomUUID().toString();
        final var bodyValues = Collections.unmodifiableList(proxyInvocation.filterPayloadArgs(PARAMETER_ANNOTATIONS));

        return new JmsDispatch() {

            @Override
            public AtDestination destination() {
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
            public AtDestination replyTo() {
                return replyTo;
            }

            @Override
            public Duration ttl() {
                return ttl;
            }

            @Override
            public Map<String, Object> properties() {
                return JmsDispatch.super.properties();
            }

        };
    }
}
