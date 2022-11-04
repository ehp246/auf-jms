package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocableTypeDefinition;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.aufjms.core.util.StreamOf;

/**
 * @author Lei Yang
 *
 */
final class DefaultInvocableScanner {
    private final static Logger LOGGER = LogManager.getLogger();

    private final PropertyResolver propertyResolver;

    public DefaultInvocableScanner(PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    public DefaultInvocableRegistry registeryFrom(final Set<String> scanPackages) {
        return new DefaultInvocableRegistry().register(perform(scanPackages).stream());
    }

    private Set<InvocableTypeDefinition> perform(final Set<String> scanPackages) {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent() || beanDefinition.getMetadata().isInterface();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(ForJmsType.class));

        return StreamOf.nonNull(scanPackages).map(scanner::findCandidateComponents).flatMap(Set::stream).map(bean -> {
            try {
                LOGGER.atTrace().log("Scanning {}", bean::getBeanClassName);

                return Class.forName(bean.getBeanClassName());
            } catch (final ClassNotFoundException e) {
                LOGGER.atError().log("This should not happen.", e);
            }
            return null;
        }).filter(Objects::nonNull).map(this::newDefinition).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private InvocableTypeDefinition newDefinition(final Class<?> type) {
        final var annotation = type.getAnnotation(ForJmsType.class);
        if (annotation == null) {
            return null;
        }

        if ((Modifier.isAbstract(type.getModifiers()) && annotation.scope().equals(InstanceScope.MESSAGE))
                || type.isEnum()) {
            throw new IllegalArgumentException("Un-instantiable type " + type.getName());
        }

        final var msgTypes = Arrays.asList(annotation.value()).stream().map(this.propertyResolver::resolve)
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
                        + invokings.get(invokingName).toString() + ", " + method.toString());
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
