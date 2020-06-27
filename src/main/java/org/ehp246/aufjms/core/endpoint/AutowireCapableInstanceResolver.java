package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.ExecutingTypeResolver;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.Msg;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Resolves an Action by the given registry to a bean/object created by the
 * given bean factory.
 * 
 * @author Lei Yang
 *
 */
public class AutowireCapableInstanceResolver implements ExecutingInstanceResolver {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final ExecutingTypeResolver typeResolver;
	private final Consumer<ExecutedInstance> executedConsumer;

	public AutowireCapableInstanceResolver(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final ExecutingTypeResolver resolver, final Consumer<ExecutedInstance> executedConsumer) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.typeResolver = resolver;
		this.executedConsumer = executedConsumer;
	}

	@Override
	public ResolvedInstance resolve(final Msg msg) {
		Objects.requireNonNull(msg);

		final var registered = this.typeResolver.resolve(msg);
		if (registered == null) {
			return null;
		}

		final Object actionInstance = registered.getScope().equals(InstanceScope.BEAN)
				? autowireCapableBeanFactory.getBean(registered.getInstanceType())
				: autowireCapableBeanFactory.createBean(registered.getInstanceType());

		return new ResolvedInstance() {

			@Override
			public Method getMethod() {
				return registered.getMethod();
			}

			@Override
			public Object getInstance() {
				return actionInstance;
			}

			@Override
			public InvocationModel getExecutionModel() {
				return registered.getExecutionModel();
			}

			@Override
			public Consumer<ExecutedInstance> postExecution() {
				return executedConsumer;
			}

		};

	}
}
