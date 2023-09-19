package me.ehp246.test.embedded.inbound.failed;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("q1"))
interface SendQ1 {
    void failedMsg(@OfCorrelationId String id);
}
