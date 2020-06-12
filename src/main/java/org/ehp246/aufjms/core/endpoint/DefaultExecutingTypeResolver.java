package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.ehp246.aufjms.api.endpoint.ExecutingTypeResolver;
import org.ehp246.aufjms.api.endpoint.ExecutionModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.MsgTypeActionDefinition;
import org.ehp246.aufjms.api.endpoint.MsgTypeActionRegistry;
import org.ehp246.aufjms.api.endpoint.ResolvedInstanceType;
import org.ehp246.aufjms.api.jms.Msg;

/**
 *
 * Action by Type Registry.
 * 
 * @author Lei Yang
 *
 */
public class DefaultExecutingTypeResolver implements MsgTypeActionRegistry, ExecutingTypeResolver {
	private final Map<String, List<MsgTypeActionDefinition>> registeredActions = new ConcurrentHashMap<>();
	private final Map<Class<?>, Map<String, Method>> registereMethods = new ConcurrentHashMap<>();

	@Override
	public void register(final MsgTypeActionDefinition actionDefinition) {
		actionDefinition.getMsgType().stream()
				.forEach(type -> registeredActions
						.computeIfAbsent(type, (key) -> new CopyOnWriteArrayList<MsgTypeActionDefinition>())
						.add(actionDefinition));

		registereMethods.put(actionDefinition.getInstanceType(), actionDefinition.getMethods());
	}

	@Override
	public List<MsgTypeActionDefinition> getRegistered() {
		return this.registeredActions.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	public List<ResolvedInstanceType> resolve(final Msg msg) {
		final var msgType = Objects.requireNonNull(Objects.requireNonNull(msg).getType());

		return this.registeredActions.keySet().stream().filter(msgType::startsWith).map(this.registeredActions::get)
				.flatMap(List::stream).map(definition -> new ResolvedInstanceType() {
					private final Class<?> instanceType = definition.getInstanceType();
					private final Method method = registereMethods.get(instanceType).get(msgType);

					@Override
					public Method getMethod() {
						return method;
					}

					@Override
					public Class<?> getInstanceType() {
						return instanceType;
					}

					@Override
					public InstanceScope getScope() {
						return definition.getScope();
					}

					@Override
					public ExecutionModel getExecutionModel() {
						return definition.getExecutionModel();
					}
				}).collect(Collectors.toList());
	}

}
