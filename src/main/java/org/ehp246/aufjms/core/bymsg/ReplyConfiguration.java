package org.ehp246.aufjms.core.bymsg;

import java.util.List;
import java.util.UUID;

import javax.jms.Message;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.MsgDispatcher;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.ReplyDestinationSupplier;
import org.ehp246.aufjms.api.jms.ToMsg;
import org.ehp246.aufjms.core.endpoint.DefaultMsgDispatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * 
 * @author Lei Yang
 *
 */
public class ReplyConfiguration {
	@Bean
	public MsgEndpoint replyEndpoint(final ReplyDestinationSupplier replyTo,
			final @Qualifier(ReplyToConfiguration.BEAN_NAME_CORRELATION_MAP) Cache<String, ResolvedInstance> correlMap,
			final ActionExecutor actionExecutor) {
		return new MsgEndpoint() {
			private final String id = UUID.randomUUID().toString();
			private final MsgDispatcher msgConsumer = new DefaultMsgDispatcher(msg -> {
				return List.of(correlMap.getIfPresent(msg.getCorrelationId()));
			}, actionExecutor);

			@Override
			public void onMessage(Message message) {
				msgConsumer.dispatch(ToMsg.wrap((message)));
			}

			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getDestinationName() {
				return null;
			}
		};
	}
}
