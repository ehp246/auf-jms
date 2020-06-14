package org.ehp246.aufjms.core.configuration;

import org.ehp246.aufjms.api.jms.MessageBodyWriter;
import org.ehp246.aufjms.api.jms.MessageCreator;
import org.ehp246.aufjms.api.jms.TextMessageCreator;
import org.springframework.context.annotation.Bean;

/**
 * @author Lei Yang
 *
 */
public class JsonMessageConfiguration {
	@Bean
	public MessageCreator<?> textMessageBuilder(MessageBodyWriter<String> bodyWriter) {
		return new TextMessageCreator(bodyWriter);
	}
}
