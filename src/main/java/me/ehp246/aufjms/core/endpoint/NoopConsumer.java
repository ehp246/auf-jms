package me.ehp246.aufjms.core.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.endpoint.MsgConsumer;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public final class NoopConsumer implements MsgConsumer {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void accept(final JmsMsg msg) {
        LOGGER.atDebug().log("Noop on: id '{}', type '{}', destination '{}'", msg::correlationId, msg::type,
                msg::destination);
    }

}
