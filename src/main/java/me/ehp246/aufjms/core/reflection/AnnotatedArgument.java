package me.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public interface AnnotatedArgument<T extends Annotation> {
    T annotation();

    Object argument();

    Parameter parameter();
}
