package org.ehp246.aufjms.core.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.ehp246.aufjms.annotation.CollectionOf;
import org.ehp246.aufjms.api.jms.FromBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * @author Lei Yang
 *
 */
class JacksonProviderTest {
	private final JacksonProvider jackson = new JacksonProvider(new ObjectMapper()
			.setSerializationInclusion(Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule()));

	private static class A {
		@CollectionOf({ Set.class, List.class, Instant.class })
		public static void m() {
		};

		public static Method getM() {
			try {
				return A.class.getMethod("m");
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Test
	void collectionOf001() {
		final var expected = List.of(Instant.now());

		final var json = jackson.to(List.of(expected));

		@SuppressWarnings("unchecked")
		final List<Instant> from = jackson.from(json, new FromBody.Receiver<List>() {

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
			public Class<List> getType() {
				return List.class;
			}
		});

		Assertions.assertEquals(true, from instanceof List);
		from.stream().forEach(value -> Assertions.assertEquals(Instant.class, value.getClass()));
	}

	@Test
	void collectionOf002() {
		final var expected = Set.of(List.of(Instant.now()));

		final var json = jackson.to(List.of(expected));

		@SuppressWarnings("unchecked")
		final Set<List<Instant>> from = jackson.from(json, new FromBody.Receiver<Set>() {

			@Override
			public List<? extends Annotation> getAnnotations() {
				return List.of(A.getM().getAnnotation(CollectionOf.class));
			}

			@Override
			public Class<Set> getType() {
				return Set.class;
			}
		});

		from.stream().forEach(list -> {
			Assertions.assertEquals(List.class, list.getClass());
			list.stream().forEach(value -> {
				Assertions.assertEquals(Instant.class, value.getClass());
			});
		});
	}

}
