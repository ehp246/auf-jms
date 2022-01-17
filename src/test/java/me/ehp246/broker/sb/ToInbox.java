package me.ehp246.broker.sb;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @At("auf-jms.inbox"), ttl = "PT24H")
interface ToInbox {
    void ping();

    void ping(int i);
}
