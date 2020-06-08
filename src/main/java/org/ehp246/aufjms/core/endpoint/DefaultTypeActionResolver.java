package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.ehp246.aufjms.api.endpoint.ExecutionModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.ResolvedTypeAction;
import org.ehp246.aufjms.api.endpoint.TypeActionDefinition;
import org.ehp246.aufjms.api.endpoint.TypeActionRegistry;
import org.ehp246.aufjms.api.endpoint.TypeActionResolver;

/**
 *
 * Action by Type Registry.
 * 
 * @author Lei Yang
 *
 */
public class DefaultTypeActionResolver implements TypeActionRegistry, TypeActionResolver {
	private final Map<String, List<TypeActionDefinition>> registeredActions = new ConcurrentHashMap<>();
	private final Map<Class<?>, Map<String, Method>> registerePerforms = new ConcurrentHashMap<>();

	@Override
	public void register(TypeActionDefinition actionDefinition) {
		actionDefinition.getType().stream().forEach(type ->
			registeredActions.getOrDefault(type, new CopyOnWriteArrayList<TypeActionDefinition>()).add(actionDefinition));
		
		registerePerforms.put(actionDefinition.getActionClass(), actionDefinition.getPerformMethods());
	}

	@Override
	public List<TypeActionDefinition> getRegistered() {
		return this.registeredActions.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	public List<ResolvedTypeAction> resolve(String type) {
		return this.registeredActions.keySet().stream().filter(type::matches).map(this.registeredActions::get).flatMap(List::stream)
				.map(definition -> new ResolvedTypeAction() {
					
					@Override
					public String getType() {
						return type;
					}
					
					@Override
					public Method getPerformMethod() {
						return registerePerforms.get(definition.getActionClass()).get(type);
					}
					
					@Override
					public Class<?> getActionClass() {
						return definition.getActionClass();
					}

					@Override
					public InstanceScope getScope() {
						return definition.getScope();
					}

					@Override
					public ExecutionModel getExecutionModel() {
						return definition.getExecutionModel();
					}
				})
				.collect(Collectors.toList());
	}
	
}
