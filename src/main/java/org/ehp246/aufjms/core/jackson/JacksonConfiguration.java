package org.ehp246.aufjms.core.jackson;

import org.ehp246.aufjms.api.endpoint.ActionInvocationBinder;
import org.ehp246.aufjms.api.endpoint.DefaultActionInvocationBinder;
import org.ehp246.aufjms.api.jms.FromBody;
import org.ehp246.aufjms.core.configuration.JsonMessageConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

@Import(JsonMessageConfiguration.class)
public class JacksonConfiguration {

	@Bean
	public ActionInvocationBinder actionInvocationBinder(FromBody<String> fromBody) {
		return new DefaultActionInvocationBinder(fromBody);
	}

	@Bean
	public JacksonProvider jacksonProvider() {
		return new JacksonProvider(new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.registerModule(new MrBeanModule()));
	}
}
