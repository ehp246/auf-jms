package me.ehp246.aufjms.integration.endpoint.deadletter;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("ref1"))
interface SendRef1 {
    void send(@OfCorrelationId String id);
}
