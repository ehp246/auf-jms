package org.ehp246.aufjms.core.endpoint;

import java.util.Set;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.ehp246.aufjms.core.formsg.ForMsgScanner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class EndpointFactory {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final Consumer<ExecutedInstance> postExecution;

	public EndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final MessagePortProvider portProvider) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.postExecution = new ReplyExecutedAction(portProvider);
	}

	public MsgEndpoint newMsgEndpoint(final String destination, final Set<String> scanPackages) {
		return new MsgEndpoint() {
			private final ExecutingInstanceResolver resolver = new AutowireCapableInstanceResolver(
					autowireCapableBeanFactory, newForMsgRegistry(scanPackages), postExecution);

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

	private DefaultExecutingTypeResolver newForMsgRegistry(Set<String> scanPackages) {
		return new DefaultExecutingTypeResolver().register(new ForMsgScanner(scanPackages).perform().stream());
	}
}
