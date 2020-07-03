package org.ehp246.aufjms.core.jackson;

import java.util.ArrayList;
import java.util.List;

import org.ehp246.aufjms.api.jms.FromBody;
import org.ehp246.aufjms.api.jms.ToBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lei Yang
 *
 */
public class JacksonProvider implements FromBody<String>, ToBody<String> {
	private final static Logger LOGGER = LoggerFactory.getLogger(JacksonProvider.class);

	private final ObjectMapper objectMapper;

	public JacksonProvider(final ObjectMapper objectMapper) {
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
				return List.of(receiveOne(body, (Receiver<Object>) receivers.get(0)));
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
		final var value = json != null && !json.isBlank() ? objectMapper.readValue(json, receiver.getType()) : null;
		receiver.receive(value);
		return value;
	}

}
