package me.ehp246.aufjms.core.jms;

import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AtQueueRecord extends AtDestinationRecord {

    public AtQueueRecord(final String name) {
        super(name, DestinationType.QUEUE);
    }
}
