package org.ehp246.aufjms.core.jackson;

import org.ehp246.aufjms.api.endpoint.ActionInvocationBinder;
import org.ehp246.aufjms.api.jms.MessageBuilder;
import org.ehp246.aufjms.api.jms.ReplyDestinationSupplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

public class JacksonConfiguration {
	public static final String BEAN_NAME_OBJECT_MAPPER = "7b5d7255-0201-42aa-93ad-e3b4f919f89b";

	@Bean(name = BEAN_NAME_OBJECT_MAPPER)
	public ObjectMapper objectMapper() {
		return new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.registerModule(new MrBeanModule());
	}

	@Bean
	public MessageBuilder messageBuilder(final ReplyDestinationSupplier replyToResolver,
			@Qualifier(BEAN_NAME_OBJECT_MAPPER) final ObjectMapper objectMapper) {
		return new JacksonMessageBuilder(replyToResolver, objectMapper);
	}

	@Bean
	public ActionInvocationBinder actionInvocationBinder(
			@Qualifier(JacksonConfiguration.BEAN_NAME_OBJECT_MAPPER) final ObjectMapper objectMapper) {
		return new JacksonActionBinder(objectMapper);
	}
}
