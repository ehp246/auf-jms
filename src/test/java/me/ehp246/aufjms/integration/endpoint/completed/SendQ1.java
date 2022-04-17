package me.ehp246.aufjms.integration.endpoint.completed;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("q1"))
interface SendQ1 {
    void send(@OfCorrelationId String id);
}
