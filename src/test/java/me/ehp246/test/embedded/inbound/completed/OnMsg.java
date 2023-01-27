package me.ehp246.test.embedded.inbound.completed;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(".*")
class OnMsg {
    @Invoking
    public String perform(@OfType final String type, @OfCorrelationId final String id) {
        if (type.equalsIgnoreCase("throw")) {
            throw new RuntimeException();
        }
        
        return id;
    }
}
