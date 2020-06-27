package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.api.endpoint.InvokingTypeResolver;
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
public class DefaultExecutingTypeResolver implements MsgTypeActionRegistry, InvokingTypeResolver {
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultExecutingTypeResolver.class);

	private final Map<String, MsgTypeActionDefinition> registeredActions = new HashMap<>();
	private final Map<Class<?>, Map<String, Method>> registereMethods = new HashMap<>();

	public DefaultExecutingTypeResolver register(final Stream<MsgTypeActionDefinition> actionDefinitions) {
		actionDefinitions.forEach(this::register);
		return this;
	}

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

		var invoking = msg.getInvoking();
		invoking = invoking != null ? invoking.strip() : "";

		final var method = registereMethods.get(definition.getInstanceType()).get(invoking);

		if (method == null) {
			LOGGER.debug("Method {} not found", invoking);
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
				return definition.getInstanceScope();
			}

			@Override
			public InvocationModel getInvocationModel() {
				return definition.getInvocationModel();
			}
		};
	}

}
