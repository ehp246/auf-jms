package in.ehp246.aufjms.core.configuration;

import org.springframework.context.annotation.Bean;

import in.ehp246.aufjms.api.endpoint.ExecutableBinder;
import in.ehp246.aufjms.api.jms.FromBody;
import in.ehp246.aufjms.api.jms.MessageCreator;
import in.ehp246.aufjms.api.jms.ToBody;
import in.ehp246.aufjms.core.endpoint.DefaultExecutableBinder;
import in.ehp246.aufjms.core.jms.TextMessageCreator;

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
