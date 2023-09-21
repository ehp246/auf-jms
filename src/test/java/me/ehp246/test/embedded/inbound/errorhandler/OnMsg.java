package me.ehp246.test.embedded.inbound.errorhandler;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.inbound.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(scope = InstanceScope.BEAN)
class OnMsg {
    public void apply(@OfProperty final int named) {
    }
}
