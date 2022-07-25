package me.ehp246.test.bulk;

import java.util.stream.IntStream;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(@To("inbox"))
interface Proxy {
    void bulkMsg(int i);

    void bulkMsg(IntStream stream);
}
