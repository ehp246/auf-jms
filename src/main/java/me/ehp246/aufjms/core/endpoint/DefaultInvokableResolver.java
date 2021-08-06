package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.MsgInvokableDefinition;
import me.ehp246.aufjms.api.endpoint.MsgInvokableRegistry;
import me.ehp246.aufjms.api.endpoint.MsgInvokableResolver;
import me.ehp246.aufjms.api.endpoint.ResolvedInstanceType;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.reflection.ReflectingType;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.StreamOf;

/**
 *
 * Invokable by Type Registry.
 *
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultInvokableResolver implements MsgInvokableRegistry, MsgInvokableResolver {
    private final static Logger LOGGER = LogManager.getLogger(DefaultInvokableResolver.class);

    private final Map<String, MsgInvokableDefinition> registeredInvokables = new HashMap<>();
    private final Map<Class<?>, Map<String, Method>> registereMethods = new HashMap<>();

    public DefaultInvokableResolver register(final Stream<MsgInvokableDefinition> invokingDefinitions) {
        invokingDefinitions.forEach(this::register);
        return this;
    }

    @Override
    public void register(final MsgInvokableDefinition invokingDefinition) {
        final var registered = registeredInvokables.putIfAbsent(invokingDefinition.getMsgType(), invokingDefinition);
        if (registered != null) {
            throw new RuntimeException(
                    "Duplicate type " + registered.getMsgType() + " from " + registered.getInstanceType());
        }

        registereMethods.put(invokingDefinition.getInstanceType(), invokingDefinition.getMethods());
    }

    @Override
    public List<MsgInvokableDefinition> getRegistered() {
        return this.registeredInvokables.values().stream().collect(Collectors.toList());
    }

    @Override
    public ResolvedInstanceType resolve(final JmsMsg msg) {
        final var msgType = OneUtil.toString(Objects.requireNonNull(msg).type(), "");

        final var definition = registeredInvokables.get(msgType);
        if (definition == null) {
            LOGGER.debug("Type {} not found", msgType);
            return null;
        }

        var invoking = msg.invoking();
        invoking = invoking != null ? invoking.strip() : "";

        final var method = registereMethods.get(definition.getInstanceType()).get(invoking);

        if (method == null) {
            LOGGER.debug("Method {} not found", invoking);
            return null;
        }

        return new ResolvedInstanceType() {

            @Override
            public Method getMethod() {
                return method;
            }

            @Override
            public Class<?> getInstanceType() {
                return definition.getInstanceType();
            }

            @Override
            public InstanceScope getScope() {
                return definition.getInstanceScope();
            }

            @Override
            public InvocationModel getInvocationModel() {
                return definition.getInvocationModel();
            }
        };
    }

    public static DefaultInvokableResolver registeryFrom(final Set<String> scanPackages) {
        return new DefaultInvokableResolver().register(perform(scanPackages).stream());
    }

    private static Set<MsgInvokableDefinition> perform(final Set<String> scanPackages) {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent() || beanDefinition.getMetadata().isInterface();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(ForJms.class));

        return StreamOf.nonNull(scanPackages).map(scanner::findCandidateComponents).flatMap(Set::stream).map(bean -> {
            try {
                LOGGER.debug("Scanning {}", bean.getBeanClassName());

                return Class.forName(bean.getBeanClassName());
            } catch (final ClassNotFoundException e) {
                LOGGER.error("This should not happen.", e);
            }
            return null;
        }).filter(Objects::nonNull).map(type -> newDefinition(type)).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static MsgInvokableDefinition newDefinition(final Class<?> instanceType) {
        final var annotation = instanceType.getAnnotation(ForJms.class);
        if (annotation == null) {
            return null;
        }

        if ((Modifier.isAbstract(instanceType.getModifiers()) && annotation.scope().equals(InstanceScope.MESSAGE))
                || instanceType.isEnum()) {
            throw new RuntimeException("Un-instantiable type " + instanceType.getName());
        }

        final var invokings = new HashMap<String, Method>();
        final var reflected = new ReflectingType<>(instanceType);

        // Search for the annotation first
        for (final var method : reflected.findMethods(Invoking.class)) {
            final var invokingName = method.getAnnotation(Invoking.class).value().strip();
            if (invokings.containsKey(invokingName)) {
                throw new RuntimeException("Duplicate executing methods found on " + instanceType.getName());
            }
            invokings.put(invokingName, method);
        }

        // There should be at least one executing method.
        if (invokings.size() == 0) {
            throw new RuntimeException("No executing method defined by " + instanceType.getName());
        }

        final var msgType = annotation.value().strip();

        LOGGER.debug("Scanned {} on {}", msgType, instanceType.getCanonicalName());

        return new MsgInvokableDefinition() {
            private final Map<String, Method> methods = Map.copyOf(invokings);

            @Override
            public String getMsgType() {
                return msgType;
            }

            @Override
            public Class<?> getInstanceType() {
                return instanceType;
            }

            @Override
            public Map<String, Method> getMethods() {
                return methods;
            }

            @Override
            public InstanceScope getInstanceScope() {
                return annotation.scope();
            }

            @Override
            public InvocationModel getInvocationModel() {
                return annotation.invocation();
            }
        };
    }
}
