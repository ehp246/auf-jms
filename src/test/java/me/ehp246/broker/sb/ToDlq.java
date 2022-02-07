package me.ehp246.broker.sb;

import java.time.Instant;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @At("auf-jms.dlq"), ttl = "PT600S")
public interface ToDlq {
    void throwIt(Instant instant);
}
