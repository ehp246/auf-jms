package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.endpoint.ExecutableTypeResolver;
import me.ehp246.aufjms.api.endpoint.ForMsgRegistry;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.InvokingDefinition;
import me.ehp246.aufjms.api.endpoint.ResolvedInstanceType;
import me.ehp246.aufjms.api.jms.Msg;

/**
 *
 * Executable by Type Registry.
 *
 * @author Lei Yang
 *
 */
public class DefaultExecutableTypeResolver implements ForMsgRegistry, ExecutableTypeResolver {
	private final static Logger LOGGER = LogManager.getLogger(DefaultExecutableTypeResolver.class);

	private final Map<String, InvokingDefinition> registeredActions = new HashMap<>();
	private final Map<Class<?>, Map<String, Method>> registereMethods = new HashMap<>();

	public DefaultExecutableTypeResolver register(final Stream<InvokingDefinition> invokingDefinitions) {
		invokingDefinitions.forEach(this::register);
		return this;
	}

	@Override
	public void register(final InvokingDefinition invokingDefinition) {
		registeredActions.put(invokingDefinition.getMsgType(), invokingDefinition);

		registereMethods.put(invokingDefinition.getInstanceType(), invokingDefinition.getMethods());
	}

	@Override
	public List<InvokingDefinition> getRegistered() {
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
