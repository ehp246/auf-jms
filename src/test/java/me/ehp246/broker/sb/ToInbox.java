package me.ehp246.broker.sb;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
@ByJms(@At("auf-jms.inbox"))
interface ToInbox {
    void ping();
}
