package org.ehp246.aufjms.core.jackson;

import java.util.List;

import org.ehp246.aufjms.api.jms.FromBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lei Yang
 *
 */
public class JacksonProvider implements FromBody<String> {
	private final static Logger LOGGER = LoggerFactory.getLogger(JacksonProvider.class);

	private final ObjectMapper objectMapper;

	public JacksonProvider(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	@Override
	public void perform(final String body, final List<Receiver> receivers) {
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
