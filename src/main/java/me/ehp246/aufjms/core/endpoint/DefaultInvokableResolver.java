package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.InvokableDefinition;
import me.ehp246.aufjms.api.endpoint.InvokableRegistry;
import me.ehp246.aufjms.api.endpoint.InvokableResolver;
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
public final class DefaultInvokableResolver implements InvokableRegistry, InvokableResolver {
    private final static Logger LOGGER = LogManager.getLogger(DefaultInvokableResolver.class);

    private final Map<String, InvokableDefinition> registeredInvokables = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String, Method>> registeredMethods = new ConcurrentHashMap<>();

    public DefaultInvokableResolver register(final Stream<InvokableDefinition> invokingDefinitions) {
        invokingDefinitions.forEach(this::register);
        return this;
    }

    @Override
    public void register(final InvokableDefinition invokingDefinition) {
        invokingDefinition.getTypes().forEach(type -> {
            final var registered = registeredInvokables.putIfAbsent(type, invokingDefinition);
            if (registered != null) {
                throw new RuntimeException("Duplicate type " + type + " from " + registered.getInstanceType());
            }

            registeredMethods.put(invokingDefinition.getInstanceType(), invokingDefinition.getMethods());
        });
    }

    @Override
    public List<InvokableDefinition> getRegistered() {
        return this.registeredInvokables.values().stream().collect(Collectors.toList());
    }

    @Override
    public ResolvedInstanceType resolve(final JmsMsg msg) {
        final var msgType = OneUtil.toString(Objects.requireNonNull(msg).type(), "");

        final var definition = registeredInvokables.entrySet().stream().filter(e -> msgType.matches(e.getKey()))
                .findAny()
                .map(Map.Entry::getValue).orElse(null);

        if (definition == null) {
            LOGGER.atTrace().log("Type {} not found", msgType);
            return null;
        }

        var invoking = msg.invoking();
        invoking = invoking != null ? invoking.strip() : "";

        final var method = registeredMethods.get(definition.getInstanceType()).get(invoking);

        if (method == null) {
            LOGGER.atTrace().log("Method {} not found", invoking);
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

    private static Set<InvokableDefinition> perform(final Set<String> scanPackages) {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent() || beanDefinition.getMetadata().isInterface();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(ForJmsType.class));

        return StreamOf.nonNull(scanPackages).map(scanner::findCandidateComponents).flatMap(Set::stream).map(bean -> {
            try {
                LOGGER.atTrace().log("Scanning {}", bean.getBeanClassName());

                return Class.forName(bean.getBeanClassName());
            } catch (final ClassNotFoundException e) {
                LOGGER.atError().log("This should not happen.", e);
            }
            return null;
        }).filter(Objects::nonNull).map(type -> newDefinition(type)).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static InvokableDefinition newDefinition(final Class<?> instanceType) {
        final var annotation = instanceType.getAnnotation(ForJmsType.class);
        if (annotation == null) {
            return null;
        }

        if ((Modifier.isAbstract(instanceType.getModifiers()) && annotation.scope().equals(InstanceScope.MESSAGE))
                || instanceType.isEnum()) {
            throw new RuntimeException("Un-instantiable type " + instanceType.getName());
        }

        final var types = Arrays.asList(annotation.value()).stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                .map(entry -> {
                    if (entry.getValue() > 1) {
                        throw new RuntimeException(
                                "Duplicate type '" + entry.getKey() + "' on " + instanceType.getCanonicalName());
                    }
                    return entry.getKey();
                }).collect(Collectors.toSet());

        final var invokings = new HashMap<String, Method>();
        final var reflected = new ReflectingType<>(instanceType);

        // Search for the annotation first
        for (final var method : reflected.findMethods(Invoking.class)) {
            final var invokingName = method.getAnnotation(Invoking.class).value().strip();
            if (invokings.containsKey(invokingName)) {
                throw new RuntimeException("Duplicate invocation methods: " + invokings.get(invokingName).toString()
                        + ", " + method.toString());
            }
            invokings.put(invokingName, method);
        }

        // Use name convention. There should be one and only.
        if (invokings.get("") == null) {
            final var invokes = reflected.findMethods("invoke");
            if (invokes.size() == 1) {
                invokings.put("", invokes.get(0));
            }
        }

        // There should be at least one method.
        if (invokings.get("") == null) {
            throw new RuntimeException("No invocation method defined by " + instanceType.getName());
        }

        LOGGER.atTrace().log("Registering {} on {}", types, instanceType.getCanonicalName());

        return new InvokableDefinition() {
            private final Map<String, Method> methods = Map.copyOf(invokings);

            @Override
            public Set<String> getTypes() {
                return types;
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
