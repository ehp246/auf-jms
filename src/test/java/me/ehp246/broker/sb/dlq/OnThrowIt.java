package me.ehp246.broker.sb.dlq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = "ThrowIt", scope = InstanceScope.BEAN)
public class OnThrowIt {
    private final static Logger LOGGER = LogManager.getLogger(OnThrowIt.class);

    @Invoking
    public void perform() {
        LOGGER.atInfo().log("Throwing it...");
        throw new RuntimeException();
    }
}
