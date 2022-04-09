package me.ehp246.aufjms.util;

import me.ehp246.aufjms.api.dispatch.InvocationDispatchConfig;
import me.ehp246.aufjms.api.jms.To;

/**
 * @author Lei Yang
 *
 */
public class MockProxyConfig implements InvocationDispatchConfig {
    private final To dest = To.toQueue("");

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
