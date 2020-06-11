package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.ExecutingTypeResolver;
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

	public AutowireCapableInstanceResolver(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final ExecutingTypeResolver resolver) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.typeResolver = resolver;
	}

	@Override
	public List<ResolvedInstance> resolve(final Msg msg) {
		Objects.requireNonNull(msg);

		final var registered = this.typeResolver.resolve(msg);
		if (registered == null || registered.size() == 0) {
			return List.of();
		}

		return registered.stream().map(one -> new ResolvedInstance() {
			final Object actionInstance = one.getScope().equals(InstanceScope.BEAN)
					? autowireCapableBeanFactory.getBean(one.getInstanceType())
					: autowireCapableBeanFactory.createBean(one.getInstanceType());

			@Override
			public Method getMethod() {
				return one.getMethod();
			}

			@Override
			public Object getInstance() {
				return actionInstance;
			}
		}).collect(Collectors.toList());

	}
}
