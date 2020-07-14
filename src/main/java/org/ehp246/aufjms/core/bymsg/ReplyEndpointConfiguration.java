package org.ehp246.aufjms.core.bymsg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.ehp246.aufjms.api.endpoint.ExecutableResolver;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import org.ehp246.aufjms.api.jms.FromBody;
import org.ehp246.aufjms.api.jms.ReplyToNameSupplier;
import org.ehp246.aufjms.core.configuration.AufJmsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ReplyEndpointConfiguration {
	private final ConcurrentMap<String, ResolvedExecutable> correlMap = new ConcurrentHashMap<>();
	private final MsgEndpoint msgEndpoint;
	private final long timeout;
	private final long ttl;
	private final ReplyToNameSupplier replyToNameSupplier;
	private final FromBody<String> fromBody;

	public ReplyEndpointConfiguration(final ReplyToNameSupplier replyTo, final FromBody<String> fromBody,
			@Value("${" + AufJmsProperties.TIMEOUT + ":" + AufJmsProperties.TIMEOUT_DEFAULT + "}") final long timeout,
			@Value("${" + AufJmsProperties.TTL + ":0}") final long ttl) {
		super();
		this.timeout = timeout;
		this.ttl = ttl;
		this.replyToNameSupplier = replyTo;
		this.fromBody = fromBody;
		this.msgEndpoint = new MsgEndpoint() {

			@Override
			public String getDestinationName() {
				return ReplyEndpointConfiguration.this.getReplyToName();
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

	public long getTtl() {
		return this.ttl;
	}

	public String getReplyToName() {
		return replyToNameSupplier.get();
	}

	public FromBody<String> getFromBody() {
		return fromBody;
	}
}
