package me.ehp246.aufjms.util;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;
import me.ehp246.aufjms.api.jms.AtDestination;

/**
 * @author Lei Yang
 *
 */
public class MockProxyConfig implements ByJmsProxyConfig {

    @Override
    public AtDestination destination() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String ttl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String context() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AtDestination replyTo() {
        // TODO Auto-generated method stub
        return null;
    }

}
