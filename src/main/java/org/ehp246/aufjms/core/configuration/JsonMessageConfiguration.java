package org.ehp246.aufjms.core.configuration;

import org.ehp246.aufjms.api.endpoint.ExecutableBinder;
import org.ehp246.aufjms.api.jms.FromBody;
import org.ehp246.aufjms.api.jms.MessageCreator;
import org.ehp246.aufjms.api.jms.ToBody;
import org.ehp246.aufjms.core.endpoint.DefaultExecutableBinder;
import org.ehp246.aufjms.core.jms.TextMessageCreator;
import org.springframework.context.annotation.Bean;

/**
 * @author Lei Yang
 *
 */
public class JsonMessageConfiguration {
	@Bean
	public MessageCreator<?> textMessageBuilder(final ToBody<String> bodyWriter) {
		return new TextMessageCreator(bodyWriter);
	}

	@Bean
	public ExecutableBinder actionInvocationBinder(final FromBody<String> fromBody) {
		return new DefaultExecutableBinder(fromBody);
	}
}
