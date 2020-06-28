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

	public JacksonProvider(ObjectMapper objectMapper) {
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
		} catch (Exception e) {
			LOGGER.error("Failed to create message: {} {}", e.getClass().getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void from(final String body, final List<Receiver> receivers) {
		if (receivers == null || receivers.size() == 0) {
			return;
		}

		try {
			// Single parameter
			if (receivers.size() == 1) {
				receiveOne(body, receivers.get(0));
				return;
			}

			// Multiple parameters
			final var jsons = objectMapper.readValue(body, String[].class);

			for (int i = 0; i < receivers.size(); i++) {
				receiveOne(jsons[i], receivers.get(i));
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Failed to read from {}", body, e);
			throw new RuntimeException(e);
		}
	}

	private void receiveOne(String json, FromBody.Receiver receiver)
			throws JsonMappingException, JsonProcessingException {
		receiver.receive(json != null && !json.isBlank() ? objectMapper.readValue(json, receiver.getType()) : null);
	}

}
