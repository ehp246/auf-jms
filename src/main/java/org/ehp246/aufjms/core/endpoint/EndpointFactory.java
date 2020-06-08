package org.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.UUID;

import javax.jms.Message;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.MsgPipe;
import org.ehp246.aufjms.api.jms.ToMsg;
import org.ehp246.aufjms.core.formsg.ForMsgScanner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class EndpointFactory {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final ActionExecutor actionExecutor;
	private final MsgPipe pipe;

	public EndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final ActionExecutor actionExecutor, final MsgPipe pipe,
			final DestinationNameResolver destinationResolver) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.actionExecutor = actionExecutor;
		this.pipe = pipe;
	}

	public MsgEndpoint newEndpoint(final String destination, final Class<?>[] scans) {
		return new MsgEndpoint() {
			private final String id = UUID.randomUUID().toString();

			private final DefaultMsgDispatcher dispatcher = new DefaultMsgDispatcher(
					new AutowireCapableTypeActionResolver(autowireCapableBeanFactory, newActionRegistry(scans)),
					actionExecutor);

			@Override
			public void onMessage(Message message) {
				dispatcher.dispatch(ToMsg.wrap(message));
			}

			@Override
			public String getDestinationName() {
				return destination;
			}

			@Override
			public String getId() {
				return id;
			}

		};
	}

	private DefaultTypeActionResolver newActionRegistry(Class<?>[] scans) {
		final var actionRegistry = new DefaultTypeActionResolver();
		new ForMsgScanner(List.of(scans)).perform().stream().forEach(actionRegistry::register);
		return actionRegistry;
	}
}
