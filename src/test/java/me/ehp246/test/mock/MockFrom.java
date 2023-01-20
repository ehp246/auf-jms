package me.ehp246.test.mock;

import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.InboundEndpoint.From.Sub;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.AtTopic;

/**
 * @author Lei Yang
 *
 */
public record MockFrom(At on, Sub sub) implements InboundEndpoint.From {
    public MockFrom(AtQueue on) {
        this(on, null);
    }

    public MockFrom(AtTopic on) {
        this(on, new Sub() {

            @Override
            public String name() {
                return null;
            }
        });
    }
}
