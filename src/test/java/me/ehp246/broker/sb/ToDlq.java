package me.ehp246.broker.sb;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("auf-jms.dlq"), ttl = "PT600S")
public interface ToDlq {
    void throwIt(Instant instant);
}
