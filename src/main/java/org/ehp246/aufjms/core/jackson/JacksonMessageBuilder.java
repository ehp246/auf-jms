package org.ehp246.aufjms.core.jackson;

import java.util.ArrayList;
import java.util.Objects;

import javax.jms.TextMessage;

import org.ehp246.aufjms.api.jms.AbstractMessageBuilder;
import org.ehp246.aufjms.api.jms.MsgSinkContext;
import org.ehp246.aufjms.api.jms.ReplyDestinationSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Lei Yang
 *
 */
public class JacksonMessageBuilder extends AbstractMessageBuilder<TextMessage> {
	private final static Logger LOGGER = LoggerFactory.getLogger(JacksonMessageBuilder.class);

	private final ObjectMapper objectMapper;

	public JacksonMessageBuilder(final ReplyDestinationSupplier replyToSupplier, final ObjectMapper objectMapper) {
		super(replyToSupplier);
		this.objectMapper = Objects.requireNonNull(objectMapper);
	}

	@Override
	protected TextMessage createMessage(MsgSinkContext sinkContext) {
		try {
			final var bodyValue = sinkContext.getMsgSupplier().getBodyValue();
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
			return sinkContext.getSession().createTextMessage(json);
		} catch (Exception e) {
			LOGGER.error("Failed to create message: {} {}", e.getClass().getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

}
