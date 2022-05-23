package me.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final class ReflectedType<T> {
    private final Class<T> type;

    public ReflectedType(final Class<T> type) {
        super();
        this.type = type;
    }

    public static <T> ReflectedType<T> reflect(final Class<T> type) {
        return new ReflectedType<T>(type);
    }

    /**
     * Returns the named method that does not have any parameter. Returns null if
     * not found.
     * 
     * @param name
     * @return
     */
    public Method findMethod(String name) {
        try {
            return type.getMethod(name, (Class<?>[]) null);
        } catch (Exception e) {
            return null;
        }
    }

    public Method findMethod(String name, Class<?>... parameters) {
        try {
            return type.getMethod(name, parameters);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns all methods that have the given name ignoring the parameters.
     * 
     * @param name
     * @return
     */
    public List<Method> findMethods(String name) {
        return Stream.of(type.getMethods()).filter(method -> method.getName().equals(name))
                .collect(Collectors.toList());
    }

    /**
     * Returns all methods that have the given annotation.
     * 
     * @param annotationClass
     * @return
     */
    public List<Method> findMethods(Class<? extends Annotation> annotationClass) {
        return Stream.of(type.getMethods()).filter(method -> method.getDeclaredAnnotation(annotationClass) != null)
                .collect(Collectors.toList());
    }

    public Class<T> getType() {
        return type;
    }

    public <A extends Annotation> Optional<A> findOnType(Class<A> annotationType) {
        return Optional.ofNullable(type.getAnnotation(annotationType));
    }
}
