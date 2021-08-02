package me.ehp246.aufjms.core.byjms;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.jms.Destination;

import me.ehp246.aufjms.api.jms.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.core.reflection.ProxyInvocation;

/**
 * @author Lei Yang
 *
 */
final class JmsDispatchFromInvocation {
    private final ByJmsProxyConfig proxyConfig;

    public JmsDispatchFromInvocation(final ByJmsProxyConfig proxyConfig) {
        super();
        this.proxyConfig = proxyConfig;
    }

    JmsDispatch from(ProxyInvocation invocation) {
        final var type = invocation.getMethodName().substring(0, 1).toUpperCase(Locale.US)
                + invocation.getMethodName().substring(1);
        final var correlId = UUID.randomUUID().toString();
        final var ttl = proxyConfig.ttl();

        return new JmsDispatch() {

            @Override
            public Destination destination() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String type() {
                return type;
            }

            @Override
            public String correlationId() {
                return correlId;
            }

            @Override
            public List<?> bodyValues() {
                return List.of();
            }

            @Override
            public Destination replyTo() {
                return null;
            }

            @Override
            public Long ttl() {
                return ttl;
            }

            @Override
            public String groupId() {
                return JmsDispatch.super.groupId();
            }

            @Override
            public Integer groupSeq() {
                return JmsDispatch.super.groupSeq();
            }

        };
    }
}
