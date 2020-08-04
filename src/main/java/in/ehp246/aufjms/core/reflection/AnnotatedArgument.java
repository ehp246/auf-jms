package in.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;

public interface AnnotatedArgument<T extends Annotation> {
	T getAnnotation();
	Object getArgument();
}
