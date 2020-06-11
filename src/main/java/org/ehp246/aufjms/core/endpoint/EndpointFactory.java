package org.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.core.formsg.ForMsgScanner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class EndpointFactory {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;

	public EndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
	}

	public MsgEndpoint newEndpoint(final String destination, final Set<String> scanPackages) {
		return new MsgEndpoint() {
			private final ExecutingInstanceResolver resolver = new AutowireCapableInstanceResolver(
					autowireCapableBeanFactory, newActionRegistry(scanPackages));

			@Override
			public String getDestinationName() {
				return destination;
			}

			@Override
			public ExecutingInstanceResolver getResolver() {
				return resolver;
			}

		};
	}

	private DefaultInstanceTypeResolver newActionRegistry(Set<String> scanPackages) {
		final var actionRegistry = new DefaultInstanceTypeResolver();
		new ForMsgScanner(scanPackages).perform().stream().forEach(actionRegistry::register);
		return actionRegistry;
	}
}
