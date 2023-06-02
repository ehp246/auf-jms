package me.ehp246.test.embedded.request.case03;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("inbox"), requestTimeout = "PT1S")
interface ClientProxy {
    int inc(int i);
}
