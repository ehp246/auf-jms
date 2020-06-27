package org.ehp246.aufjms.core.jackson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ehp246.aufjms.api.jms.ToBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Lei Yang
 *
 */
public class JacksonBodyWriter implements ToBody<String> {
	private final static Logger LOGGER = LoggerFactory.getLogger(JacksonBodyWriter.class);

	private final ObjectMapper objectMapper;

	public JacksonBodyWriter(final ObjectMapper objectMapper) {
		this.objectMapper = Objects.requireNonNull(objectMapper);
	}

	@Override
	public String perform(final List<?> bodyValue) {
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

}
