package org.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.ehp246.aufjms.api.endpoint.ActionInstanceResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.core.formsg.ForMsgScanner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class EndpointFactory {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;

	public EndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final DestinationNameResolver destinationResolver) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
	}

	public MsgEndpoint newEndpoint(final String destination, final Set<String> scanPackages) {
		return new MsgEndpoint() {
			private final ActionInstanceResolver resolver = new AutowireCapableTypeActionResolver(
					autowireCapableBeanFactory, newActionRegistry(scanPackages));

			@Override
			public String getDestinationName() {
				return destination;
			}

			@Override
			public ActionInstanceResolver getResolver() {
				return resolver;
			}

		};
	}

	private DefaultTypeActionResolver newActionRegistry(Set<String> scanPackages) {
		final var actionRegistry = new DefaultTypeActionResolver();
		new ForMsgScanner(scanPackages).perform().stream().forEach(actionRegistry::register);
		return actionRegistry;
	}
}
