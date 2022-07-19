package me.ehp246.broker.sb.session;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfProperty;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("auf-jms.session"))
interface SessionedQueue {
    void send(@OfProperty("JMSXGroupID") String id);
}
