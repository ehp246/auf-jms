package me.ehp246.test.embedded.log4jcontext;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.OfLog4jContext;
import me.ehp246.aufjms.api.annotation.OfLog4jContext.Op;
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

    void ping(@OfLog4jContext Name name);

    void ping(@OfLog4jContext Name name, @OfProperty @OfLog4jContext String nameProperty);

    @OfType("Ping")
    void pingWithName(@OfLog4jContext("WithName.") Name name);

    @OfType("Ping")
    void pingIntroWithName(@OfLog4jContext(value = "WithName.", op = Op.Introspect) Name name);

    @OfType("Ping")
    void pingIntro(@OfLog4jContext(op = Op.Introspect) Name name);

    void ping2(@OfLog4jContext @OfProperty int accountId, Order order);

    void pingOnBody(Order order);

    record Name(@OfLog4jContext String firstName, @OfLog4jContext String lastName) {
    }

    record Order(@OfLog4jContext int id, @OfLog4jContext int amount) {
    }
}
