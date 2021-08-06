package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import me.ehp246.aufjms.core.reflection.ReflectingType;
import me.ehp246.aufjms.core.util.StreamOf;

/**
 *
 * @author Lei Yang
 *
 */
final class ForJmsScanner {
    private final static Logger LOGGER = LogManager.getLogger(ForJmsScanner.class);

    private ForJmsScanner(final Set<String> scanPackages) {
        super();
    }

    static Set<MsgInvokableDefinition> perform(final Set<String> scanPackages) {
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

    static private MsgInvokableDefinition newDefinition(final Class<?> instanceType) {
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
