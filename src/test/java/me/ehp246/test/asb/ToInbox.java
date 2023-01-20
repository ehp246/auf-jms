package me.ehp246.test.asb;

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
