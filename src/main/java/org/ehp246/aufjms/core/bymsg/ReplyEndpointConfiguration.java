package org.ehp246.aufjms.core.bymsg;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.ehp246.aufjms.api.endpoint.ExecutableResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import org.ehp246.aufjms.api.jms.FromBody;
import org.ehp246.aufjms.api.jms.ReplyToNameSupplier;
import org.ehp246.aufjms.core.configuration.AufJmsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.github.benmanes.caffeine.cache.Caffeine;

public class ReplyEndpointConfiguration {
	private final ConcurrentMap<@NonNull String, @NonNull ResolvedExecutable> correlMap;
	private final MsgEndpoint msgEndpoint;
	private final long timeout;
	private final ReplyToNameSupplier replyToNameSupplier;
	private final FromBody<String> fromBody;

	public ReplyEndpointConfiguration(ReplyToNameSupplier replyTo,
			@Value("${" + AufJmsProperties.TIMEOUT + ":" + AufJmsProperties.TIMEOUT_DEFAULT + "}") long timeout,
			final FromBody<String> fromBody) {
		super();
		this.timeout = timeout;
		this.replyToNameSupplier = replyTo;
		this.fromBody = fromBody;
		this.correlMap = Caffeine.newBuilder().weakValues().<String, ResolvedExecutable>build().asMap();
		this.msgEndpoint = new MsgEndpoint() {

			@Override
			public String getDestinationName() {
				return replyTo.get();
			}

			@Override
			public ExecutableResolver getResolver() {
				return msg -> correlMap.get(msg.getCorrelationId());
			}

		};

	}

	public Map<String, ResolvedExecutable> getCorrelMap() {
		return correlMap;
	}

	@Bean
	public MsgEndpoint getReplyEndpoint() {
		return msgEndpoint;
	}

	public long getTimeout() {
		return this.timeout;
	}

	public String getReplyToName() {
		return replyToNameSupplier.get();
	}

	public FromBody<String> getFromBody() {
		return fromBody;
	}
}
