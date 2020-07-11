package org.ehp246.aufjms.core.formsg;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.api.endpoint.ForMsgExecutableDefinition;
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
	private static class MsgTypeActionDefinitionImplementation implements ForMsgExecutableDefinition {
		private final ForMsg annotation;
		private final String msgType;
		private final Class<?> instanceType;
		private final Map<String, Method> methods;

		private MsgTypeActionDefinitionImplementation(final HashMap<String, Method> invokings, final ForMsg annotation,
				final String msgType, final Class<?> instanceType) {
			this.annotation = annotation;
			this.msgType = msgType;
			this.instanceType = instanceType;
			this.methods = Map.copyOf(invokings);
		}

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
		public InstanceScope getInstanceScope() {
			return annotation.scope();
		}

		@Override
		public InvocationModel getInvocationModel() {
			return annotation.invocation();
		}
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(ForMsgScanner.class);

	private final Set<String> scanPackages;

	public ForMsgScanner(final Set<String> scanPackages) {
		super();
		this.scanPackages = scanPackages;
	}

	public Set<ForMsgExecutableDefinition> perform() {
		final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
			@Override
			protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
				return beanDefinition.getMetadata().isIndependent() || beanDefinition.getMetadata().isInterface();
			}
		};
		scanner.addIncludeFilter(new AnnotationTypeFilter(ForMsg.class));

		return StreamOf.nonNull(scanPackages).map(scanner::findCandidateComponents).flatMap(Set::stream).map(bean -> {
			try {
				LOGGER.debug("Scanning {}", bean.getBeanClassName());

				return Class.forName(bean.getBeanClassName());
			} catch (final ClassNotFoundException e) {
				LOGGER.error("This should not happen.", e);
			}
			return null;
		}).filter(Objects::nonNull).map(this::newDefinition).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	private ForMsgExecutableDefinition newDefinition(final Class<?> instanceType) {
		final var annotation = instanceType.getAnnotation(ForMsg.class);
		if (annotation == null) {
			return null;
		}

		if ((Modifier.isAbstract(instanceType.getModifiers()) && annotation.scope().equals(InstanceScope.MESSAGE))
				|| instanceType.isEnum()) {
			throw new RuntimeException("Un-instantiable type " + instanceType.getName());
		}

		final var invokings = new HashMap<String, Method>();
		final var reflected = new ReflectingType<>(instanceType);

		// Search for the annotation first
		for (final var method : reflected.findMethods(Invoking.class)) {
			final var invokingName = Optional.of(method.getAnnotation(Invoking.class).value().strip())
					.filter(name -> name.length() > 0).orElseGet(method::getName);
			if (invokings.containsKey(invokingName)) {
				throw new RuntimeException("Duplicate executing methods found on " + instanceType.getName());
			}
			invokings.put(invokingName, method);
		}

		// There should be at least one executing method.
		if (invokings.size() == 0) {
			throw new RuntimeException("No executing method defined by " + instanceType.getName());
		}

		// Annotation value takes precedence.Falls back to class name if no value is
		// specified.
		final var msgType = annotation.value().strip().length() == 0 ? instanceType.getSimpleName()
				: annotation.value();

		LOGGER.debug("Scanned {} on {}", msgType, instanceType.getCanonicalName());

		return new MsgTypeActionDefinitionImplementation(invokings, annotation, msgType, instanceType);
	}
}
