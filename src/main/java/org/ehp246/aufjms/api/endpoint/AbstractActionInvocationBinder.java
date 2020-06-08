package org.ehp246.aufjms.api.endpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.jms.Message;

import org.ehp246.aufjms.annotation.OfCorrelationId;
import org.ehp246.aufjms.annotation.OfProperty;
import org.ehp246.aufjms.annotation.OfType;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.reflection.ReflectingInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public abstract class AbstractActionInvocationBinder implements ActionInvocationBinder {
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractActionInvocationBinder.class);

	protected static final Map<Class<? extends Annotation>, Function<Msg, String>> HEADER_VALUE_SUPPLIERS = Map
			.of(OfCorrelationId.class, Msg::getCorrelationId, OfType.class, Msg::getType);

	protected static final Set<Class<? extends Annotation>> HEADER_ANNOTATIONS = Set
			.copyOf(HEADER_VALUE_SUPPLIERS.keySet());

	@Override
	public ReflectingInvocation bind(final ResolvedInstance resolved, final ActionInvocationContext ctx) {
		final var method = resolved.getMethod();
		if (method.getParameterCount() == 0) {
			return new ReflectingInvocation(resolved.getInstance(), method, null);
		}

		final var parameters = method.getParameters();
		final var arguments = new Object[parameters.length];

		final var boundMarkers = bindContextArgs(method.getParameters(), ctx, arguments);

		final var bodyArgPositions = new ArrayList<Integer>();
		for (int i = 0; i < boundMarkers.length; i++) {
			if (boundMarkers[i]) {
				continue;
			}
			bodyArgPositions.add(i);
		}

		if (bodyArgPositions.size() > 0) {
			try {
				bindBodyArgs(parameters, bodyArgPositions, ctx.getMsg(), arguments);
			} catch (Exception e) {
				LOGGER.error("Failed to bind body arguments", e);
				throw new RuntimeException();
			}
		}

		return new ReflectingInvocation(resolved.getInstance(), method, arguments);
	}

	protected abstract void bindBodyArgs(Parameter[] parameters, List<Integer> bodyArgPositions, final Msg mq,
			Object[] arguments) throws Exception;

	/**
	 * Fills in the context arguments at the index position. Returns indices of
	 * positions that have been filled.
	 * 
	 * @param parameters
	 * @param mq
	 * @param arguments
	 * @return
	 */
	private boolean[] bindContextArgs(Parameter[] parameters, final ActionInvocationContext ctx, Object[] arguments) {
		final boolean[] markers = new boolean[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			final var parameter = parameters[i];
			final var mq = ctx.getMsg();

			// Bind by type
			final var type = parameter.getType();
			if (type.isAssignableFrom(Msg.class)) {
				arguments[i] = mq;
				markers[i] = true;
			} else if (type.isAssignableFrom(Message.class)) {
				arguments[i] = mq.getMessage();
				markers[i] = true;
			}

			// Bind Headers
			final var annotations = parameter.getAnnotations();
			var found = Stream.of(annotations)
					.filter(annotation -> HEADER_ANNOTATIONS.contains(annotation.annotationType())).findAny();
			if (found.isPresent()) {
				arguments[i] = HEADER_VALUE_SUPPLIERS.get(found.get().annotationType()).apply(mq);
				markers[i] = true;
			}

			// Bind Properties
			found = Stream.of(annotations).filter(annotation -> annotation.annotationType() == OfProperty.class)
					.findAny();
			if (found.isPresent()) {
				arguments[i] = mq.getProperty(((OfProperty) found.get()).value(), parameter.getType());
				markers[i] = true;
			}
		}

		return markers;
	}
}
