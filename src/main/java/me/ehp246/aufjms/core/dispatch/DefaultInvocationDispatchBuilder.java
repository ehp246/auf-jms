package me.ehp246.aufjms.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDelay;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfTtl;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.dispatch.ByJmsConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.reflection.AnnotatedArgument;
import me.ehp246.aufjms.core.reflection.DefaultProxyInvocation;
import me.ehp246.aufjms.core.reflection.ReflectedArgument;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocationDispatchBuilder implements InvocationDispatchBuilder {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfType.class, OfProperty.class,
            OfTtl.class, OfDelay.class, OfCorrelationId.class);
    private final PropertyResolver propertyResolver;

    public DefaultInvocationDispatchBuilder(final PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    @Override
    public JmsDispatch get(final Object proxy, final Method method, final Object[] args, final ByJmsConfig config) {
        final var proxyInvocation = new DefaultProxyInvocation(method.getDeclaringClass(), proxy, method, args);

        // Destination is required.
        final var destination = config.to() instanceof AtQueue ? At.toQueue(config.to().name())
                : At.toTopic(config.to().name());

        // Optional.
        final var replyTo = Optional.ofNullable(config.replyTo())
                .map(at -> at instanceof AtQueue ? At.toQueue(at.name()) : At.toTopic(at.name())).orElse(null);

        // In the priority of Argument, Method, Type.
        final String type = proxyInvocation.resolveAnnotatedValue(OfType.class,
                arg -> arg.argument() != null ? arg.argument().toString()
                        : arg.annotation().value().isBlank() ? null : arg.annotation().value(),
                ofMethod -> ofMethod.value().isBlank() ? OneUtil.firstUpper(proxyInvocation.getMethodName())
                        : ofMethod.value(),
                ofClass -> ofClass.value().isBlank() ? proxyInvocation.getDeclaringClassSimpleName() : ofClass.value(),
                () -> OneUtil.firstUpper(proxyInvocation.getMethodName()));

        final var properties = new HashMap<String, Object>();

        proxyInvocation.streamOfAnnotatedArguments(OfProperty.class)
                .forEach(new Consumer<AnnotatedArgument<OfProperty>>() {
                    @Override
                    public void accept(final AnnotatedArgument<OfProperty> annoArg) {
                        final var key = annoArg.annotation().value();
                        final var value = annoArg.argument();
                        // Must have a property name for non-map values.
                        if (!OneUtil.hasValue(key) && !annoArg.parameter().getType().isAssignableFrom(Map.class)) {
                            throw new RuntimeException("Un-defined property name on parameter " + annoArg.parameter());
                        }
                        // Skip null maps.
                        if (annoArg.parameter().getType().isAssignableFrom(Map.class) && value == null) {
                            return;
                        }
                        newValue(key, value);
                    }

                    @SuppressWarnings("unchecked")
                    private void newValue(final String key, final Object newValue) {
                        // Ignoring annotation value.
                        if (newValue instanceof Map) {
                            properties.putAll(((Map<String, Object>) newValue));
                            return;
                        }

                        properties.put(key.toString(), newValue);
                    }
                });

        final var delaySpecified = Optional.ofNullable(proxyInvocation.resolveAnnotatedValue(OfDelay.class,
                arg -> arg.argument() != null ? arg.argument().toString()
                        : arg.annotation().value().isBlank() ? null
                                : propertyResolver.resolve(arg.annotation().value()),
                ofDelay -> propertyResolver.resolve(ofDelay.value()),
                ofDelay -> propertyResolver.resolve(ofDelay.value()), () -> null)).orElse(null);

        // Accepts null.
        final var ttlSpecified = Optional.ofNullable(proxyInvocation.resolveAnnotatedValue(OfTtl.class,
                arg -> arg.argument() != null ? arg.argument().toString()
                        : arg.annotation().value().isBlank() ? null : arg.annotation().value(),
                OfTtl::value, a -> null, () -> null)).map(propertyResolver::resolve).orElse(null);

        final var correlId = proxyInvocation.firstArgumentAnnotationOf(OfCorrelationId.class,
                annoArg -> Optional.ofNullable(annoArg.argument()).map(Object::toString).orElse(null),
                () -> UUID.randomUUID().toString());

        final var bodyArg = proxyInvocation.filterPayloadArgs(PARAMETER_ANNOTATIONS).stream().findFirst()
                .orElseGet(() -> new ReflectedArgument(null, null, null));

        return new JmsDispatch() {
            private final Duration ttl = ttlSpecified == null ? config.ttl()
                    : OneUtil.hasValue(ttlSpecified) ? Duration.parse(ttlSpecified) : null;
            private final Duration delay = delaySpecified == null ? config.delay()
                    : OneUtil.hasValue(delaySpecified) ? Duration.parse(delaySpecified) : null;

            @Override
            public At to() {
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
            public Object body() {
                return bodyArg.argument();
            }

            @Override
            public BodyAs bodyAs() {
                return () -> bodyArg.parameter().getType();
            }

            @Override
            public At replyTo() {
                return replyTo;
            }

            @Override
            public Duration ttl() {
                return ttl;
            }

            @Override
            public Map<String, Object> properties() {
                return properties;
            }

            @Override
            public Duration delay() {
                return delay;
            }

        };
    }
}
