package me.ehp246.test.asb.sub;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To(value = "auf-jms.echo.event", type = DestinationType.TOPIC))
public interface ToDirectTopic {
    void echo(@OfCorrelationId String id);
}
