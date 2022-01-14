package me.ehp246.aufjms.util;

import me.ehp246.aufjms.api.dispatch.DispatchConfig;
import me.ehp246.aufjms.api.jms.AtDestination;
import me.ehp246.aufjms.core.jms.AtQueueRecord;

/**
 * @author Lei Yang
 *
 */
public class MockProxyConfig implements DispatchConfig {
    private final AtDestination dest = new AtQueueRecord("");

    @Override
    public AtDestination destination() {
        return dest;
    }

    @Override
    public String ttl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AtDestination replyTo() {
        // TODO Auto-generated method stub
        return null;
    }

}
