package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.At;

/**
 * @author Lei Yang
 *
 */
public record ByJmsProxyHandler(At to, At replyTo, Duration ttl, Duration delay, String connectionFactory) implements InvocationHandler, ByJmsProxyConfig {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

}
