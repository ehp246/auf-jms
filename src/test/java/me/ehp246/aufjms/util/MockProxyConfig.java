package me.ehp246.aufjms.util;

import me.ehp246.aufjms.api.dispatch.InvocationDispatchConfig;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueueRecord;

/**
 * @author Lei Yang
 *
 */
public class MockProxyConfig implements InvocationDispatchConfig {
    private final At dest = new AtQueueRecord("");

    @Override
    public At to() {
        return dest;
    }

    @Override
    public String ttl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public At replyTo() {
        // TODO Auto-generated method stub
        return null;
    }

}
