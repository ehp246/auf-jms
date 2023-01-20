package me.ehp246.aufjms.provider.jackson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.spi.FromJson;
import me.ehp246.aufjms.api.spi.ToJson;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class JsonByObjectMapper implements FromJson, ToJson {
    private final static Logger LOGGER = LogManager.getLogger(JsonByObjectMapper.class);

    private final ObjectMapper objectMapper;

    public JsonByObjectMapper(final ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    public String apply(final List<ToJson.From> values) {
        try {
            String json = null;
            if (values == null) {
                json = null;
            } else if (values.size() == 1) {
                final var target = values.get(0);
                json = this.objectMapper.writerFor(target.type()).writeValueAsString(target.value());
            } else if (values.size() > 1) {
                // Use wrapping array for multi-parameter only.
                final var list = new ArrayList<String>(values.size());
                for (int i = 0; i < values.size(); i++) {
                    final var target = values.get(i);
                    list.add(this.objectMapper.writerFor(target.type()).writeValueAsString(target.value()));
                }
                json = this.objectMapper.writeValueAsString(list);
            }
            return json;
        } catch (final Exception e) {
            LOGGER.atError().log("Failed to serialize: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<?> apply(final String body, final List<To> receivers) {
        if (receivers == null || receivers.size() == 0) {
            return List.of();
        }

        try {
            // Single parameter
            if (receivers.size() == 1) {
                // List.of does not take null.
                return Arrays.asList(new Object[] { receiveOne(body, receivers.get(0)) });
            }

            // Multiple parameters
            final var jsons = objectMapper.readValue(body, String[].class);
            final var values = new ArrayList<Object>();

            for (int i = 0; i < receivers.size(); i++) {
                values.add(receiveOne(jsons[i], receivers.get(i)));
            }

            return values;
        } catch (final JsonProcessingException e) {
            LOGGER.error("Failed to read from {}", body, e);
            throw new RuntimeException(e);
        }
    }

    private Object receiveOne(final String json, final FromJson.To receiver)
            throws JsonMappingException, JsonProcessingException {
        return json != null && !json.isBlank() ? readOne(json, receiver) : null;
    }

    private Object readOne(final String json, final To receiver) throws JsonMappingException, JsonProcessingException {
        return objectMapper.readValue(json, receiver.type());
    }
}
