package me.ehp246.test.embedded.endpoint.failedmsg;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("q2"))
interface SendQ2 {
    void send(@OfCorrelationId String id);
}
