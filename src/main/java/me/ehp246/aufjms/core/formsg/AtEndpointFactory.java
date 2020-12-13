package me.ehp246.aufjms.core.formsg;

import java.util.Set;
import java.util.function.Consumer;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.MsgEndpoint;
import me.ehp246.aufjms.api.jms.MsgPortProvider;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableTypeResolver;
import me.ehp246.aufjms.core.endpoint.ReplyExecuted;

/**
 *
 * @author Lei Yang
 *
 */
public class AtEndpointFactory {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final Consumer<ExecutedInstance> reply;

	public AtEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final MsgPortProvider portProvider) {
		super();
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.reply = new ReplyExecuted(portProvider);
	}

	public MsgEndpoint newMsgEndpoint(final String destination, final Set<String> scanPackages) {
		return new MsgEndpoint() {
			private final ExecutableResolver resolver = new AutowireCapableInstanceResolver(autowireCapableBeanFactory,
					newForMsgRegistry(scanPackages), reply);

			@Override
			public String getDestinationName() {
				return destination;
			}

			@Override
			public ExecutableResolver getResolver() {
				return resolver;
			}

		};
	}

	private DefaultExecutableTypeResolver newForMsgRegistry(final Set<String> scanPackages) {
		return new DefaultExecutableTypeResolver().register(new ForMsgScanner(scanPackages).perform().stream());
	}
}
