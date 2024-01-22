package me.ehp246.aufjms.core.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufjms.api.inbound.MsgConsumer;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
public final class NoOpConsumer implements MsgConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(NoOpConsumer.class);

    @Override
    public void accept(final JmsMsg msg) {
        LOGGER.atDebug().setMessage("No op on: id '{}', type '{}', destination '{}'")
                .addArgument(msg::correlationId).addArgument(msg::type)
                .addArgument(msg::destination).log();
    }

}
