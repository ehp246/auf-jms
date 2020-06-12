package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ehp246.aufjms.api.endpoint.ExecutingTypeResolver;
import org.ehp246.aufjms.api.endpoint.ExecutionModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.MsgTypeActionDefinition;
import org.ehp246.aufjms.api.endpoint.MsgTypeActionRegistry;
import org.ehp246.aufjms.api.endpoint.ResolvedInstanceType;
import org.ehp246.aufjms.api.jms.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Action by Type Registry.
 * 
 * @author Lei Yang
 *
 */
public class DefaultExecutingTypeResolver implements MsgTypeActionRegistry, ExecutingTypeResolver {
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultExecutingTypeResolver.class);

	private final Map<String, MsgTypeActionDefinition> registeredActions = new HashMap<>();
	private final Map<Class<?>, Map<String, Method>> registereMethods = new HashMap<>();

	@Override
	public void register(final MsgTypeActionDefinition actionDefinition) {
		registeredActions.put(actionDefinition.getMsgType(), actionDefinition);

		registereMethods.put(actionDefinition.getInstanceType(), actionDefinition.getMethods());
	}

	@Override
	public List<MsgTypeActionDefinition> getRegistered() {
		return this.registeredActions.values().stream().collect(Collectors.toList());
	}

	@Override
	public ResolvedInstanceType resolve(final Msg msg) {
		final var msgType = Objects.requireNonNull(Objects.requireNonNull(msg).getType()).strip();

		final var definition = registeredActions.get(msgType);
		if (definition == null) {
			LOGGER.debug("Type {} not found", msgType);
			return null;
		}

		final var executing = Optional.ofNullable(msg.getExecuting()).map(String::strip).orElse("");

		final var method = registereMethods.get(definition.getInstanceType()).get(executing);

		if (method == null) {
			LOGGER.debug("Method {} not found", executing);
			return null;
		}

		return new ResolvedInstanceType() {

			@Override
			public Method getMethod() {
				return method;
			}

			@Override
			public Class<?> getInstanceType() {
				return definition.getInstanceType();
			}

			@Override
			public InstanceScope getScope() {
				return definition.getScope();
			}

			@Override
			public ExecutionModel getExecutionModel() {
				return definition.getExecutionModel();
			}
		};
	}

}
