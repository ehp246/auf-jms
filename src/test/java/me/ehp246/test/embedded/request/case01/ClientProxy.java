package me.ehp246.test.embedded.request.case01;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("inbox"))
interface ClientProxy {
    int inc(int i);

    Person swapName(Person person);
}
