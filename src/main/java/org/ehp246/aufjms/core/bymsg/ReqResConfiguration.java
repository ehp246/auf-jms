package org.ehp246.aufjms.core.bymsg;

import java.util.Map;

import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.ReplyToNameSupplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.github.benmanes.caffeine.cache.Caffeine;

public class ReqResConfiguration {
	public static final String BEAN_NAME_CORRELATION_MAP = "215904e1-b1c5-4754-8dd2-62c64497b204";

	@Bean(name = BEAN_NAME_CORRELATION_MAP)
	public Map<String, ResolvedInstance> correlMap() {
		return Caffeine.newBuilder().weakValues().<String, ResolvedInstance>build().asMap();
	}

	@Bean
	public MsgEndpoint resEndpoint(final ReplyToNameSupplier replyTo,
			final @Qualifier(ReqResConfiguration.BEAN_NAME_CORRELATION_MAP) Map<String, ResolvedInstance> correlMap) {
		return new MsgEndpoint() {
			private final ExecutingInstanceResolver resolver = msg -> correlMap.get(msg.getCorrelationId());

			@Override
			public String getDestinationName() {
				return replyTo.get();
			}

			@Override
			public ExecutingInstanceResolver getResolver() {
				return resolver;
			}

		};
	}

}
