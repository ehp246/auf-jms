package me.ehp246.test.embedded.log4jcontext;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfMDC;
import me.ehp246.aufjms.api.annotation.OfMDC.Op;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To(TestQueueListener.DESTINATION_NAME))
interface Log4jContextCase {
    void ping(@OfProperty String aufJmsLog4jContextOrderId);

    void ping(@OfMDC Name name);

    void ping(@OfMDC Name name, @OfProperty @OfMDC String nameProperty);

    @OfType("Ping")
    void pingWithName(@OfMDC("WithName.") Name name);

    @OfType("Ping")
    void pingIntroWithName(@OfMDC(value = "WithName.", op = Op.Introspect) Name name);

    @OfType("Ping")
    void pingIntro(@OfMDC(op = Op.Introspect) Name name);

    void ping2(@OfMDC @OfProperty int accountId, Order order);

    void pingOnBody(Order order);

    record Name(@OfMDC String firstName, @OfMDC String lastName) {
    }

    record Order(@OfMDC int id, @OfMDC int amount) {
    }
}
