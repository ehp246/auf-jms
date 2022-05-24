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
import me.ehp246.aufjms.api.endpoint.InvocableType;
import me.ehp246.aufjms.api.endpoint.InvocableTypeDefinition;
import me.ehp246.aufjms.api.endpoint.InvocableTypeRegistry;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.core.util.StreamOf;

/**
 *
 * Invokable by Type Registry.
 *
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultInvocableRegistry implements InvocableTypeRegistry {
    private final static Logger LOGGER = LogManager.getLogger(DefaultInvocableRegistry.class);

    private final Map<String, InvocableTypeDefinition> registeredInvokables = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String, Method>> registeredMethods = new ConcurrentHashMap<>();

    public DefaultInvocableRegistry register(final Stream<InvocableTypeDefinition> invokingDefinitions) {
        invokingDefinitions.forEach(this::register);
        return this;
    }

    @Override
    public void register(final InvocableTypeDefinition invokingDefinition) {
        invokingDefinition.msgTypes().forEach(type -> {
            final var registered = registeredInvokables.putIfAbsent(type, invokingDefinition);
            if (registered != null) {
                throw new IllegalArgumentException("Duplicate type " + type + " from " + registered.type());
            }

            registeredMethods.put(invokingDefinition.type(), invokingDefinition.methods());
        });
    }

    @Override
    public List<InvocableTypeDefinition> registered() {
        return this.registeredInvokables.values().stream().collect(Collectors.toList());
    }

    @Override
    public InvocableType resolve(final JmsMsg msg) {
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

        final var method = registeredMethods.get(definition.type()).get(invoking);

        if (method == null) {
            LOGGER.atTrace().log("Method {} not found", invoking);
            return null;
        }

        return new InvocableType(definition.type(), method, definition.scope(), definition.model());
    }

    public static DefaultInvocableRegistry registeryFrom(final Set<String> scanPackages) {
        return new DefaultInvocableRegistry().register(perform(scanPackages).stream());
    }

    private static Set<InvocableTypeDefinition> perform(final Set<String> scanPackages) {
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

    private static InvocableTypeDefinition newDefinition(final Class<?> type) {
        final var annotation = type.getAnnotation(ForJmsType.class);
        if (annotation == null) {
            return null;
        }

        if ((Modifier.isAbstract(type.getModifiers()) && annotation.scope().equals(InstanceScope.MESSAGE))
                || type.isEnum()) {
            throw new IllegalArgumentException("Un-instantiable type " + type.getName());
        }

        final var msgTypes = Arrays.asList(annotation.value()).stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                .map(entry -> {
                    if (entry.getValue() > 1) {
                        throw new IllegalArgumentException(
                                "Duplicate type '" + entry.getKey() + "' on " + type.getCanonicalName());
                    }
                    return entry.getKey();
                }).collect(Collectors.toSet());

        final var invokings = new HashMap<String, Method>();
        final var reflected = new ReflectedType<>(type);

        // Search for the annotation first
        for (final var method : reflected.findMethods(Invoking.class)) {
            final var invokingName = method.getAnnotation(Invoking.class).value().strip();
            if (invokings.containsKey(invokingName)) {
                throw new IllegalArgumentException("Duplicate invocation methods: "
                        + invokings.get(invokingName).toString()
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
            throw new IllegalArgumentException("No invocation method defined by " + type.getName());
        }

        LOGGER.atTrace().log("Registering {} on {}", msgTypes::toString, type::getCanonicalName);

        return new InvocableTypeDefinition(msgTypes, type, Map.copyOf(invokings), annotation.scope(),
                annotation.invocation());
    }
}
