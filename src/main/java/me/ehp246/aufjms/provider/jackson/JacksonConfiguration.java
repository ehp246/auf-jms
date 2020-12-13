package me.ehp246.aufjms.provider.jackson;

import java.util.Optional;

import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Qualified class name is referenced in the import selector.
 *
 * @author Lei Yang
 *
 */
public class JacksonConfiguration {

	@Bean
	public JsonByJackson jacksonProvider(final Optional<ObjectMapper> optional) {
		return new JsonByJackson(optional.orElseGet(() -> new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
	}
}
