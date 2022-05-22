package me.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedMethod {
    private final Class<?> declaringType;
    private final Method method;
    private final Parameter[] parameters;

    public ReflectedMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.declaringType = method.getDeclaringClass();
        this.parameters = method.getParameters();
    }

    public <A extends Annotation, V> ValueSupplier resolveSupplier(final Class<A> annotationClass,
            final Function<A, String> mapper, final ValueSupplier.SimpleSupplier defValue) {
        return firstParameterWith(annotationClass).map(i -> (ValueSupplier.IndexSupplier) i::intValue)
                .map(s -> (ValueSupplier) s).orElseGet(() -> {
                    final var value = Optional.ofNullable(findOnMethodUp(annotationClass)).map(mapper::apply);
                    return value.isEmpty() ? defValue : (ValueSupplier.SimpleSupplier) value::get;
                });
    }

    public <A extends Annotation, V> ValueSupplier resolveSupplierOnArgs(final Class<A> annotationClass,
            final ValueSupplier.SimpleSupplier defValue) {
        return firstParameterWith(annotationClass).map(i -> (ValueSupplier.IndexSupplier) i::intValue)
                .map(s -> (ValueSupplier) s).orElse(defValue);
    }

    public Integer firstPayloadParameter(final Set<Class<? extends Annotation>> exclusions) {
        for (var i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (exclusions.stream().filter(type -> parameter.isAnnotationPresent(type)).findAny().isEmpty()) {
                return i;
            }
        }

        return null;
    }

    public Optional<Integer> firstParameterWith(final Class<? extends Annotation> annotationClass) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(annotationClass)) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    public void allParametersWith(final Class<? extends Annotation> annotationType, final ParameterConsumer consumer) {
        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (parameter.isAnnotationPresent(annotationType)) {
                consumer.accept(parameter, i);
            }
        }
    }

    public <A extends Annotation, V> V methodAnnotationOf(final Class<A> annotationClass, final Function<A, V> mapper) {
        final var found = method.getAnnotation(annotationClass);
        return found == null ? null : mapper.apply(found);
    }

    public Method method() {
        return this.method;
    }

    public Parameter getParameter(int index) {
        return this.parameters[index];
    }

    public <A extends Annotation> A findOnMethodUp(final Class<A> annotationClass) {
        final var found = method.getAnnotation(annotationClass);
        if (found != null) {
            return found;
        }

        return declaringType.getAnnotation(annotationClass);
    }
}
