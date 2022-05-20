package me.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.core.dispatch.ValueSupplier;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedMethod {
    private final Class<?> declaringType;
    private final Method method;
    private final Annotation[][] parameterAnnotations;
    private final Class<?>[] threws;
    private final Parameter[] parameters;

    public ReflectedMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.declaringType = method.getDeclaringClass();
        this.parameterAnnotations = this.method.getParameterAnnotations();
        this.threws = this.method.getExceptionTypes();
        this.parameters = method.getParameters();
    }

    public <A extends Annotation, V> ValueSupplier resolveSupplier(final Class<A> annotationClass,
            final Function<A, V> mapper) {
        return firstArgumentAnnotationOf(OfType.class).map(i -> (ValueSupplier.IndexSupplier) i::intValue)
                .map(s -> (ValueSupplier) s).orElseGet(() -> {
                    final var value = findOnUp(annotationClass);
                    return (ValueSupplier.StaticSupplier) () -> value == null ? OneUtil.firstUpper(method.getName())
                            : mapper.apply(value);
                });
    }

    public Optional<Integer> firstArgumentAnnotationOf(final Class<? extends Annotation> annotationClass) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(annotationClass)) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    public <A extends Annotation, V> V methodAnnotationOf(final Class<A> annotationClass, final Function<A, V> mapper) {
        final var found = method.getAnnotation(annotationClass);
        return found == null ? null : mapper.apply(found);
    }

    public Method method() {
        return this.method;
    }

    public <A extends Annotation> A findOnUp(final Class<A> annotationClass) {
        final var found = method.getAnnotation(annotationClass);
        if (found != null) {
            return found;
        }

        return declaringType.getAnnotation(annotationClass);
    }
}
