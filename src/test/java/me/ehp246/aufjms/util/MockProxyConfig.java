package me.ehp246.aufjms.util;

import me.ehp246.aufjms.api.dispatch.InvocationDispatchConfig;
import me.ehp246.aufjms.api.jms.To;
import me.ehp246.aufjms.api.jms.ToQueueRecord;

/**
 * @author Lei Yang
 *
 */
public class MockProxyConfig implements InvocationDispatchConfig {
    private final To dest = new ToQueueRecord("");

    @Override
    public To to() {
        return dest;
    }

    @Override
    public String ttl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public To replyTo() {
        // TODO Auto-generated method stub
        return null;
    }

}
