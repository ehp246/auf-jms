package me.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedProxyMethod {
    private final Class<?> declaringType;
    private final Method method;
    private final Parameter[] parameters;
    private final List<Class<?>> exceptionTypes;

    public ReflectedProxyMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.declaringType = method.getDeclaringClass();
        this.parameters = method.getParameters();
        this.exceptionTypes = List.of(method.getExceptionTypes());
    }

    public Optional<ReflectedParameter> firstPayloadParameter(final Set<Class<? extends Annotation>> exclusions) {
        for (var i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (exclusions.stream().filter(type -> parameter.isAnnotationPresent(type)).findAny().isEmpty()) {
                return Optional.of(new ReflectedParameter(parameter, i));
            }
        }

        return Optional.empty();
    }

    public List<ReflectedParameter> allParametersWith(final Class<? extends Annotation> annotationType) {
        final var list = new ArrayList<ReflectedParameter>();

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (parameter.isAnnotationPresent(annotationType)) {
                list.add(new ReflectedParameter(parameter, i));
            }
        }

        return list;
    }

    public Method method() {
        return this.method;
    }

    public Parameter getParameter(final int index) {
        return this.parameters[index];
    }

    public <A extends Annotation> Optional<A> findOnMethodUp(final Class<A> annotationClass) {
        final var found = method.getAnnotation(annotationClass);
        if (found != null) {
            return Optional.of(found);
        }

        return Optional.ofNullable(declaringType.getAnnotation(annotationClass));
    }

    /**
     * Is the given type on the <code>throws</code>. Must be explicitly declared.
     * Not on the clause doesn't mean the exception can not be thrown by the method,
     * e.g., all runtime exceptions.
     */
    public boolean isOnThrows(final Class<?> type) {
        return RuntimeException.class.isAssignableFrom(type)
                || this.exceptionTypes.stream().filter(t -> t.isAssignableFrom(type)).findAny().isPresent();
    }

    public boolean returnsVoid() {
        final var returnType = method.getReturnType();

        return returnType == void.class || returnType == Void.class;
    }
}
