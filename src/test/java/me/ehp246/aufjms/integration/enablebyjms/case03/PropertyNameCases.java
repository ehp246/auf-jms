package me.ehp246.aufjms.integration.enablebyjms.case03;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
public interface PropertyNameCases {
    @ByJms(value = @To("${to.name}"))
    interface ToNameCase01 {
    }

    @ByJms(value = @To(""), ttl = "${name}")
    interface TtlCase01 {
    }

    @ByJms(value = @To(""), ttl = "${name}", replyTo = @To("${replyTo}"))
    interface ReplyToCase01 {
    }
}
