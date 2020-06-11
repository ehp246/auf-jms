package org.ehp246.aufjms.core.formsg;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.ehp246.aufjms.annotation.Executing;
import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.api.endpoint.ExecutionModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.MsgTypeActionDefinition;
import org.ehp246.aufjms.core.reflection.ReflectingType;
import org.ehp246.aufjms.util.StreamOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * 
 * @author Lei Yang
 *
 */
public class ForMsgScanner {
	private final static Logger LOGGER = LoggerFactory.getLogger(ForMsgScanner.class);

	private final Set<String> scanPackages;

	public ForMsgScanner(Set<String> scanPackages) {
		super();
		this.scanPackages = scanPackages;
	}

	public Set<MsgTypeActionDefinition> perform() {
		final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				return beanDefinition.getMetadata().isIndependent() || beanDefinition.getMetadata().isInterface();
			}
		};
		scanner.addIncludeFilter(new AnnotationTypeFilter(ForMsg.class));

		return StreamOf.nonNull(scanPackages).map(scanner::findCandidateComponents).flatMap(Set::stream).map(bean -> {
			try {
				LOGGER.debug("Registering {}", bean.getBeanClassName());

				return Class.forName(bean.getBeanClassName());
			} catch (ClassNotFoundException e) {
				LOGGER.error("This should not happen.", e);
			}
			return null;
		}).filter(Objects::nonNull).map(this::newDefinition).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	private MsgTypeActionDefinition newDefinition(final Class<?> instanceType) {
		final var annotation = instanceType.getAnnotation(ForMsg.class);
		if (annotation == null) {
			return null;
		}

		if ((Modifier.isAbstract(instanceType.getModifiers()) && annotation.scope().equals(InstanceScope.MESSAGE))
				|| instanceType.isEnum()) {
			throw new RuntimeException("Un-instantiable type " + instanceType.getName());
		}

		final var exes = new HashMap<String, Method>();
		final var reflected = new ReflectingType<>(instanceType);

		// Search for the annotation first
		reflected.findMethods(Executing.class).stream().forEach(method -> {
			final String[] matchTypes = method.getAnnotation(Executing.class).value();
			if (matchTypes.length == 0) {
				if (exes.containsKey(instanceType.getSimpleName())) {
					throw new RuntimeException("Duplicate executing methods found on " + instanceType.getName());
				}
				exes.put(instanceType.getSimpleName(), method);
			} else {
				final var intersect = new HashSet<String>(exes.keySet());
				intersect.retainAll(Set.of(matchTypes));
				if (intersect.size() > 0) {
					throw new RuntimeException(
							"Duplicate types '" + intersect.toString() + "' found on " + instanceType.getName());
				} else {
					Arrays.stream(matchTypes).forEach(type -> exes.put(type, method));
				}
			}
		});

		// No annotated methods found. Fall back to name convention
		if (exes.size() == 0) {
			final var found = reflected.findMethods("execute");
			if (found.size() > 1) {
				throw new RuntimeException("Duplicate by-convention methods found on " + instanceType.getName());
			}
			if (found.size() == 1) {
				exes.put(instanceType.getSimpleName(), found.get(0));
			}
		}

		// There should be at least one Perform.
		if (exes.size() == 0) {
			throw new RuntimeException("No executing defined by " + instanceType.getName());
		}

		// Types from annotation or simple class name.
		final var msgTypes = Set.of(
				annotation.value().length == 0 ? new String[] { instanceType.getSimpleName() } : annotation.value());

		if (msgTypes.size() == 0) {
			throw new RuntimeException("No type defined by " + instanceType.getName());
		}

		return new MsgTypeActionDefinition() {
			private final Map<String, Method> methods = Map.copyOf(exes);

			@Override
			public Set<String> getMsgType() {
				return msgTypes;
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
			public InstanceScope getScope() {
				return annotation.scope();
			}

			@Override
			public ExecutionModel getExecutionModel() {
				return annotation.execution();
			}
		};

	}
}
