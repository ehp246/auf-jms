package org.ehp246.aufjms.core.bymsg;

import java.util.List;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.ReplyDestinationSupplier;
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
			final ExecutingInstanceResolver resolver = msg -> List.of(correlMap.getIfPresent(msg.getCorrelationId()));

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
