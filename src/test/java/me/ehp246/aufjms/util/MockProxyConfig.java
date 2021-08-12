package me.ehp246.aufjms.util;

import java.time.Duration;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;

/**
 * @author Lei Yang
 *
 */
public class MockProxyConfig implements ByJmsProxyConfig {

    @Override
    public String destination() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Duration ttl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String connection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String replyTo() {
        // TODO Auto-generated method stub
        return null;
    }

}
