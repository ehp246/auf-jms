package org.ehp246.aufjms.core.formsg;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
				LOGGER.debug("Scanning {}", bean.getBeanClassName());

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

		final var executings = new HashMap<String, Method>();
		final var reflected = new ReflectingType<>(instanceType);

		// Search for the annotation first
		for (Method method : reflected.findMethods(Executing.class)) {
			final var executing = Optional.of(method.getAnnotation(Executing.class).value().strip())
					.filter(name -> name.length() > 0).orElse("");
			if (executings.containsKey(executing)) {
				throw new RuntimeException("Duplicate executing methods found on " + instanceType.getName());
			}
			executings.put(executing, method);
		}

		// No annotated methods found. Fall back to name convention
		if (executings.size() == 0) {
			final var found = reflected.findMethods("execute");
			if (found.size() > 1) {
				throw new RuntimeException("Duplicate by-convention methods found on " + instanceType.getName());
			}
			if (found.size() == 1) {
				executings.put("", found.get(0));
			}
		}

		// There should be at least one executing method.
		if (executings.size() == 0) {
			throw new RuntimeException("No executing method defined by " + instanceType.getName());
		}

		// Annotation value takes precedence.Falls back to class name if no value is
		// specified.
		final var msgType = annotation.value().strip().length() == 0 ? instanceType.getSimpleName()
				: annotation.value();

		LOGGER.debug("Scanned {} on {}", msgType, instanceType.getCanonicalName());

		return new MsgTypeActionDefinition() {
			private final Map<String, Method> methods = Map.copyOf(executings);

			@Override
			public String getMsgType() {
				return msgType;
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
