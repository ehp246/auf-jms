package in.ehp246.aufjms.core.jackson;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

import me.ehp246.aufjms.api.annotation.CollectionOf;
import me.ehp246.aufjms.api.jms.FromBody;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
@RunWith(JUnitPlatform.class)
class JsonByJacksonTest {
	private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule());
	private final JsonByJackson jackson = new JsonByJackson(objectMapper);

	@Test
	void collectionOf001() throws JsonMappingException, JsonProcessingException {
		final var expected = List.of(Set.of(List.of(Instant.now(), Instant.now())));

		final var json = jackson.to(List.of(expected));

		final var from = jackson.from(json, new FromBody.Receiver<>() {

			@Override
			public List<? extends Annotation> getAnnotations() {
				return List.of(new CollectionOf() {

					@Override
					public Class<? extends Annotation> annotationType() {
						return null;
					}

					@Override
					public Class<?>[] value() {
						return new Class<?>[] { Set.class, List.class, Instant.class };
					}
				});
			}

			@Override
			public Class<?> getType() {
				return List.class;
			}
		});

		Assertions.assertEquals(true, from instanceof List);

		((List<?>) from).stream().forEach(set -> {
			Assertions.assertEquals(true, set instanceof Set);
			((Set<?>) set).stream().forEach(list -> {
				Assertions.assertEquals(true, list instanceof List);
				Assertions.assertEquals(2, ((List<?>) list).size());
				((List<?>) list).stream().forEach(value -> {
					Assertions.assertEquals(true, value instanceof Instant);
				});
			});
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void collectionOf002() {
		final var expected = Set.of(Instant.now());

		final var json = jackson.to(List.of(expected));

		final var from = jackson.from(json, new FromBody.Receiver<Set>() {

			@Override
			public List<? extends Annotation> getAnnotations() {
				return List.of(new CollectionOf() {

					@Override
					public Class<? extends Annotation> annotationType() {
						return null;
					}

					@Override
					public Class<?>[] value() {
						return new Class<?>[] { Instant.class };
					}
				});
			}

			@Override
			public Class<Set> getType() {
				return Set.class;
			}
		});

		from.stream().forEach(instant -> Assertions.assertEquals(Instant.class, instant.getClass()));
	}

}
