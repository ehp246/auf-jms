package org.ehp246.aufjms.core.jackson;

import java.lang.reflect.Parameter;
import java.util.List;

import org.ehp246.aufjms.api.endpoint.AbstractActionInvocationBinder;
import org.ehp246.aufjms.api.jms.Msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Lei Yang
 *
 */
public class JacksonActionBinder extends AbstractActionInvocationBinder {
	private final ObjectMapper objectMapper;

	public JacksonActionBinder(final ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	@Override
	protected void bindBodyArgs(final Parameter[] parameters, final List<Integer> positions, final Msg mq,
			final Object[] arguments) throws JsonMappingException, JsonProcessingException {
		// Single parameter
		if (positions.size() == 1) {
			final var position = positions.get(0);
			arguments[position] = objectMapper.readValue(mq.getBodyAsText(), parameters[position].getType());
			return;
		}

		// Multiple parameters
		final var jsons = objectMapper.readValue(mq.getBodyAsText(), String[].class);
		for (int i = 0; i < positions.size(); i++) {
			final var position = positions.get(i);
			arguments[position] = objectMapper.readValue(jsons[position], parameters[position].getType());
		}
	}

}
