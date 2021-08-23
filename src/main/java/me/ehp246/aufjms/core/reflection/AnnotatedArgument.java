package me.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface AnnotatedArgument<T extends Annotation> {
    T annotation();

    Object argument();

    Parameter parameter();
}
