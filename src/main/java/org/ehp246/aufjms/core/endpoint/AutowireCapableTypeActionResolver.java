package org.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.ehp246.aufjms.api.endpoint.ActionInstanceResolver;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.endpoint.TypeActionResolver;
import org.ehp246.aufjms.api.jms.Msg;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Resolves an Action by the given registry to a bean/object created by the
 * given bean factory.
 * 
 * @author Lei Yang
 *
 */
public class AutowireCapableTypeActionResolver implements ActionInstanceResolver {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final TypeActionResolver actionResolver;

	public AutowireCapableTypeActionResolver(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final TypeActionResolver resolver) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.actionResolver = resolver;
	}

	@Override
	public List<ResolvedInstance> get(Msg msg) {
		final var registered = this.actionResolver.resolve(msg.getType());
		if (registered == null || registered.size() == 0) {
			return null;
		}

		return registered.stream().map(one -> new ResolvedInstance() {
			final Object actionInstance = one.getScope().equals(InstanceScope.BEAN)
					? autowireCapableBeanFactory.getBean(one.getActionClass())
					: autowireCapableBeanFactory.createBean(one.getActionClass());

			@Override
			public Method getMethod() {
				return one.getPerformMethod();
			}

			@Override
			public Object getInstance() {
				return actionInstance;
			}
		}).collect(Collectors.toList());

	}
}
