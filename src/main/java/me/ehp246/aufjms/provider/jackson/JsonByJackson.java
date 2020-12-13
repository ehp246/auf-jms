package me.ehp246.aufjms.provider.jackson;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.annotation.CollectionOf;
import me.ehp246.aufjms.api.jms.FromBody;
import me.ehp246.aufjms.api.jms.ToBody;

/**
 * @author Lei Yang
 *
 */
public class JsonByJackson implements FromBody<String>, ToBody<String> {
	private final static Logger LOGGER = LoggerFactory.getLogger(JsonByJackson.class);

	private final ObjectMapper objectMapper;

	public JsonByJackson(final ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	@Override
	public String to(final List<?> bodyValue) {
		try {
			String json = null;
			if (bodyValue == null) {
				json = null;
			} else if (bodyValue.size() == 1) {
				json = this.objectMapper.writeValueAsString(bodyValue.get(0));
			} else if (bodyValue.size() > 1) {
				// Use wrapping array for multi-parameter only.
				final var list = new ArrayList<String>(bodyValue.size());
				for (int i = 0; i < bodyValue.size(); i++) {
					list.add(this.objectMapper.writeValueAsString(bodyValue.get(i)));
				}
				json = this.objectMapper.writeValueAsString(list);
			}
			return json;
		} catch (final Exception e) {
			LOGGER.error("Failed to create message: {} {}", e.getClass().getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<?> from(final String body, final List<Receiver<?>> receivers) {
		if (receivers == null || receivers.size() == 0) {
			return List.of();
		}

		try {
			// Single parameter
			if (receivers.size() == 1) {
				// List.of does not take null.
				return Arrays.asList(new Object[] { receiveOne(body, (Receiver<Object>) receivers.get(0)) });
			}

			// Multiple parameters
			final var jsons = objectMapper.readValue(body, String[].class);
			final var values = new ArrayList<Object>();

			for (int i = 0; i < receivers.size(); i++) {
				values.add(receiveOne(jsons[i], (Receiver<Object>) receivers.get(i)));
			}

			return values;
		} catch (final JsonProcessingException e) {
			LOGGER.error("Failed to read from {}", body, e);
			throw new RuntimeException(e);
		}
	}

	private Object receiveOne(final String json, final FromBody.Receiver<Object> receiver)
			throws JsonMappingException, JsonProcessingException {
		final var value = json != null && !json.isBlank() ? readOne(json, receiver) : null;
		receiver.receive(value);
		return value;
	}

	private Object readOne(final String json, final Receiver<Object> receiver)
			throws JsonMappingException, JsonProcessingException {
		final var collectionOf = receiver.getAnnotations() == null ? null
				: receiver.getAnnotations().stream().filter(ann -> ann instanceof CollectionOf).findAny()
						.map(ann -> ((CollectionOf) ann).value()).orElse(null);

		if (collectionOf == null) {
			return objectMapper.readValue(json, receiver.getType());
		}
		if (collectionOf.length == 1) {
			return objectMapper.readValue(json,
					objectMapper.getTypeFactory().constructParametricType(receiver.getType(), collectionOf));
		} else {
			final var typeFactory = objectMapper.getTypeFactory();
			final var types = new ArrayList<Class<?>>();
			types.add(receiver.getType());
			types.addAll(List.of(collectionOf));

			final var size = types.size();
			var type = typeFactory.constructParametricType(types.get(size - 2), types.get(size - 1));
			for (int i = size - 3; i >= 0; i--) {
				type = typeFactory.constructParametricType(types.get(i), type);
			}
			return objectMapper.readValue(json, type);
		}
	}
}
