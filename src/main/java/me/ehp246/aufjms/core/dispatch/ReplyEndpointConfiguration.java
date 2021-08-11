package me.ehp246.aufjms.core.dispatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.endpoint.AtEndpoint;
import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.jms.ReplyToNameSupplier;
import me.ehp246.aufjms.api.spi.FromJson;
import me.ehp246.aufjms.core.configuration.AufJmsProperties;

public class ReplyEndpointConfiguration {
    private final ConcurrentMap<String, Executable> correlMap = new ConcurrentHashMap<>();
    private final AtEndpoint msgEndpoint;
    private final long timeout;
    private final long ttl;
    private final ReplyToNameSupplier replyToNameSupplier;
    private final FromJson fromBody;

    public ReplyEndpointConfiguration(final ReplyToNameSupplier replyTo, final FromJson fromBody,
            @Value("${" + AufJmsProperties.TIMEOUT + ":" + AufJmsProperties.TIMEOUT_DEFAULT + "}") final long timeout,
            @Value("${" + AufJmsProperties.TTL + ":0}") final long ttl) {
        super();
        this.timeout = timeout;
        this.ttl = ttl;
        this.replyToNameSupplier = replyTo;
        this.fromBody = fromBody;
        this.msgEndpoint = new AtEndpoint() {

            @Override
            public String destination() {
                return ReplyEndpointConfiguration.this.getReplyToName();
            }

            @Override
            public ExecutableResolver resolver() {
                return msg -> correlMap.get(msg.correlationId());
            }

            @Override
            public String connection() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String concurrency() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String name() {
                // TODO Auto-generated method stub
                return null;
            }

        };

    }

    public Map<String, Executable> getCorrelMap() {
        return correlMap;
    }

    @Bean
    public AtEndpoint getReplyEndpoint() {
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

    public FromJson getFromBody() {
        return fromBody;
    }
}
