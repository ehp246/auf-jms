package me.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface AnnotatedParameterConsumer<A extends Annotation> {
    void accept(Parameter parameter, int index, A annotation);
}
