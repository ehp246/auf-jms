package me.ehp246.aufjms.core.inbound;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.inbound.InvocableTypeDefinition;
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

    public DefaultInvocableScanner(final PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    public DefaultInvocableRegistry registeryFrom(final Class<?>[] classes, final Set<String> scanPackages) {
        // Registering first, then scanning
        final var all = Stream.concat(Optional.ofNullable(classes).map(List::of).orElseGet(List::of).stream()
                .map(this::newDefinition).collect(Collectors.toSet()).stream(), perform(scanPackages).stream());

        return new DefaultInvocableRegistry().register(all);
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
                return Class.forName(bean.getBeanClassName());
            } catch (final ClassNotFoundException e) {
                LOGGER.atError().withThrowable(e).log("This should not happen: {}", e::getMessage);
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

        final var msgTypes = Arrays
                .asList(annotation.value().length == 0 ? new String[] { type.getSimpleName() } : annotation.value())
                .stream().map(this.propertyResolver::resolve)
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

        /*
         * Look for 'invoke', 'apply' in this order. There should be only one of either.
         */
        if (invokings.get("") == null) {
            final var invokes = reflected.findMethods("invoke");
            if (invokes.size() == 1) {
                invokings.put("", invokes.get(0));
            } else {
                final var applies = reflected.findMethods("apply");
                if (applies.size() == 1) {
                    invokings.put("", applies.get(0));
                }
            }
        }

        // There should be at least one method.
        if (invokings.get("") == null) {
            throw new IllegalArgumentException("No invocation method defined by " + type.getName());
        }

        return new InvocableTypeDefinition(msgTypes, type, Map.copyOf(invokings), annotation.scope(),
                annotation.invocation());
    }
}
