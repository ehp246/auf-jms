package me.ehp246.broker.sb;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("auf-jms.inbox"), ttl = "PT24H")
interface ToInbox {
    void ping();

    void ping(int i);
}
