package org.ehp246.aufjms.core.configuration;

import org.ehp246.aufjms.api.jms.ToBody;
import org.ehp246.aufjms.api.jms.MessageCreator;
import org.ehp246.aufjms.api.jms.TextMessageCreator;
import org.springframework.context.annotation.Bean;

/**
 * @author Lei Yang
 *
 */
public class JsonMessageConfiguration {
	@Bean
	public MessageCreator<?> textMessageBuilder(ToBody<String> bodyWriter) {
		return new TextMessageCreator(bodyWriter);
	}
}
