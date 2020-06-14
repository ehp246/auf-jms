package org.ehp246.aufjms.core.bymsg;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.RespondToDestinationSupplier;
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
	public MsgEndpoint replyEndpoint(final RespondToDestinationSupplier replyTo,
			final @Qualifier(ReqResConfiguration.BEAN_NAME_CORRELATION_MAP) Cache<String, ResolvedInstance> correlMap,
			final ActionExecutor actionExecutor) {
		return new MsgEndpoint() {
			final ExecutingInstanceResolver resolver = msg -> correlMap.getIfPresent(msg.getCorrelationId());

			@Override
			public String getDestinationName() {
				// TODO
				return null;
			}

			@Override
			public ExecutingInstanceResolver getResolver() {
				return resolver;
			}

		};
	}
}
