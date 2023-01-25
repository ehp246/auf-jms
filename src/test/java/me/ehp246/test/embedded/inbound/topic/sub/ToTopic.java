package me.ehp246.test.embedded.inbound.topic.sub;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To(value = "TOPIC", type = DestinationType.TOPIC))
public interface ToTopic {
    void msg(@OfCorrelationId String id);
}
