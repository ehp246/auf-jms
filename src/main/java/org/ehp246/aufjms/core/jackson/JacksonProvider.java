package org.ehp246.aufjms.core.jackson;

import java.util.List;

import org.ehp246.aufjms.api.jms.FromBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
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
		if (body == null || body.isBlank()) {
			return;
		}

		if (receivers == null || receivers.size() == 0) {
			return;
		}

		try {
			if (receivers.size() == 1) {
				final var receiver = receivers.get(0);
				receiver.receive(objectMapper.readValue(body, receiver.getType()));
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Failed to read value", e);
			throw new RuntimeException(e);
		}
	}

}
