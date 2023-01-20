package me.ehp246.test.asb.session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@ForJmsType("*")
class OnMsg {
    private final static Logger LOGGER = LogManager.getLogger();

    public void invoke(final JmsMsg msg) {
        LOGGER.atDebug().log("Group id: {}", msg::groupId);
    }
}
