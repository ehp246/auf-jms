package me.ehp246.broker.sb.dlq.trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(value = "ThrowIt")
public class OnThrowIt {
    private final static Logger LOGGER = LogManager.getLogger(OnThrowIt.class);

    @Invoking
    public void perform() {
        LOGGER.atInfo().log("Throwing it...");
        throw new RuntimeException();
    }
}
