package org.ehp246.aufjms.core.formsg;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.api.endpoint.ExecutionModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.TypeActionDefinition;
import org.ehp246.aufjms.util.StreamOf;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

/**
 * 
 * @author Lei Yang
 *
 */
public class ForMsgScanner {
	private final Set<String> scanPackages;

	public ForMsgScanner(Set<String> scanPackages) {
		super();
		this.scanPackages = scanPackages;
	}

	public Set<TypeActionDefinition> perform() {
		final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				return beanDefinition.getMetadata().isIndependent() || beanDefinition.getMetadata().isInterface();
			}
		};
		scanner.addIncludeFilter(new AnnotationTypeFilter(ForMsg.class));

		return StreamOf.nonNull(scanPackages).map(scanner::findCandidateComponents).flatMap(Set::stream).map(bean -> {
			try {
				return Class.forName(bean.getBeanClassName());
			} catch (ClassNotFoundException e) {
				// Should not happen
			}
			return null;
		}).filter(Objects::nonNull).map(this::newDefinition).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	private TypeActionDefinition newDefinition(Class<?> actionClass) {
		final var annotation = actionClass.getAnnotation(ForMsg.class);
		if (annotation == null) {
			return null;
		}

		if ((Modifier.isAbstract(actionClass.getModifiers()) && annotation.scope().equals(InstanceScope.MESSAGE))
				|| actionClass.isEnum()) {
			throw new RuntimeException("Un-instantiable Action " + actionClass.getName());
		}

		final var performs = PerformMethodScanner.mapPerform(actionClass);

		// There should be at least one Perform.
		if (performs.size() == 0) {
			throw new RuntimeException("No Perform defined by " + actionClass.getName());
		}

		// Types from annotation or simple class name.
		final var types = Arrays
				.stream(Optional.of(annotation.value()).filter(type -> type.length > 0)
						.orElseGet(() -> new String[] { actionClass.getSimpleName() }))
				.filter(StringUtils::hasText).collect(Collectors.toSet());

		if (types.size() == 0) {
			throw new RuntimeException("No Type defined by " + actionClass.getName());
		}

		return new TypeActionDefinition() {

			@Override
			public Set<String> getType() {
				return types;
			}

			@Override
			public Class<?> getActionClass() {
				return actionClass;
			}

			@Override
			public Map<String, Method> getPerformMethods() {
				return performs;
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
