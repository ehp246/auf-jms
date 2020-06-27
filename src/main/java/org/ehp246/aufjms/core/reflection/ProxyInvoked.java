package org.ehp246.aufjms.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ProxyInvoked<T> {
	private final T target;
	private final Method method;
	private final List<?> args;
	private final Annotation[][] parameterAnnotations;

	public ProxyInvoked(final T target, final Method method, final Object[] args) {
		this.target = target;
		this.method = Objects.requireNonNull(method);
		this.args = Collections.unmodifiableList(args == null ? new ArrayList<Object>() : Arrays.asList(args));
		this.parameterAnnotations = this.method.getParameterAnnotations();
	}

	public T getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}

	public List<?> getArgs() {
		return args;
	}

	public Class<?> getReturnType() {
		return this.method.getReturnType();
	}

	public boolean isReturnDeclared() {
		return this.method.getReturnType() != void.class;
	}

	/**
	 * Void is considered a declared return.
	 * 
	 * @return
	 */
	public List<Class<?>> getThrows() {
		return List.of(this.method.getExceptionTypes());
	}

	public List<?> filterValueArgs(final Set<Class<? extends Annotation>> annotations) {
		final var valueArgs = new ArrayList<>();
		for (var i = 0; i < parameterAnnotations.length; i++) {
			if (Stream.of(parameterAnnotations[i])
					.filter(annotation -> annotations.contains(annotation.annotationType())).findAny().isPresent()) {
				continue;
			}
			valueArgs.add(args.get(i));
		}

		return valueArgs;
	}

	/**
	 * Returns the value of the annotation or default if annotation is not found.
	 * 
	 * @param <A>
	 * @param <V>
	 * @param annotationClass
	 * @param mapper
	 * @return
	 */
	public <A extends Annotation, V> Optional<A> findOnDeclaringClass(final Class<A> annotationClass) {
		return Optional.ofNullable(this.method.getDeclaringClass().getAnnotation(annotationClass));
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> Optional<AnnotatedArgument<A>> findUpArgument(final Class<A> annotationClass) {
		for (int i = 0; i < parameterAnnotations.length; i++) {
			final var found = Stream.of(parameterAnnotations[i])
					.filter(annotation -> annotation.annotationType() == annotationClass).findFirst();
			if (found.isPresent()) {
				final var arg = args.get(i);
				return Optional.of(new AnnotatedArgument<A>() {

					@Override
					public A getAnnotation() {
						return (A) found.get();
					}

					@Override
					public Object getArgument() {
						return arg;
					}

				});
			}
		}
		return Optional.empty();
	}

	public <A extends Annotation, V> Optional<V> optionalValueOnMethod(final Class<A> annotationClass,
			Function<A, V> mapper) {
		return Optional.ofNullable(this.findOnMethod(annotationClass).map(mapper).orElse(null));
	}

	/**
	 * Returns the value of the annotation on method or the provided default.
	 * 
	 * @param <A>
	 * @param <V>
	 * @param annotationClass
	 * @param mapper
	 * @param def
	 * @return
	 */
	public <A extends Annotation, V> V annotationValueOnMethod(final Class<A> annotationClass, Function<A, V> mapper,
			Supplier<V> supplier) {
		return this.findOnMethod(annotationClass).map(mapper).orElseGet(supplier);
	}

	public <A extends Annotation> Optional<A> findOnMethod(final Class<A> annotationClass) {
		return Optional.ofNullable(method.getAnnotation(annotationClass));
	}

	public String getMethodName() {
		return method.getName();
	}

	public String getMethodNameCapped() {
		final var type = this.method.getName();
		return type.substring(0, 1).toUpperCase() + type.substring(1);
	}

	public String getSimpleClassName() {
		return target.getClass().getSimpleName();
	}
}
