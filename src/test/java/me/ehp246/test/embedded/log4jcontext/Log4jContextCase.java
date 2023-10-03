package me.ehp246.test.embedded.log4jcontext;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfLog4jContext;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To(TestQueueListener.DESTINATION_NAME))
interface Log4jContextCase {
    void ping(@OfProperty String aufJmsLog4jContextOrderId);

    void ping2(@OfLog4jContext @OfProperty int accountId, Order order);

    record Order(@OfLog4jContext int id, @OfLog4jContext int amount) {
    }
}
