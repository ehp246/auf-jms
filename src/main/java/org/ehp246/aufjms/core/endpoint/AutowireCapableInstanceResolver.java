package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.ExecutableResolver;
import org.ehp246.aufjms.api.endpoint.InvokingTypeResolver;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import org.ehp246.aufjms.api.jms.Msg;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Resolves an Action by the given registry to a bean/object created by the
 * given bean factory.
 * 
 * @author Lei Yang
 *
 */
public class AutowireCapableInstanceResolver implements ExecutableResolver {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final InvokingTypeResolver typeResolver;
	private final Consumer<ExecutedInstance> executedConsumer;

	public AutowireCapableInstanceResolver(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final InvokingTypeResolver resolver, final Consumer<ExecutedInstance> executedConsumer) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.typeResolver = resolver;
		this.executedConsumer = executedConsumer;
	}

	@Override
	public ResolvedExecutable resolve(final Msg msg) {
		Objects.requireNonNull(msg);

		final var registered = this.typeResolver.resolve(msg);
		if (registered == null) {
			return null;
		}

		final Object actionInstance = registered.getScope().equals(InstanceScope.BEAN)
				? autowireCapableBeanFactory.getBean(registered.getInstanceType())
				: autowireCapableBeanFactory.createBean(registered.getInstanceType());

		return new ResolvedExecutable() {

			@Override
			public Method getMethod() {
				return registered.getMethod();
			}

			@Override
			public Object getInstance() {
				return actionInstance;
			}

			@Override
			public InvocationModel getInvocationModel() {
				return registered.getInvocationModel();
			}

			@Override
			public Consumer<ExecutedInstance> postExecution() {
				return executedConsumer;
			}

		};

	}
}
