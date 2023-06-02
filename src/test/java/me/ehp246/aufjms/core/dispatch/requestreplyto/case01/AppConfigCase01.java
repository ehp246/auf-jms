package me.ehp246.aufjms.core.dispatch.requestreplyto.case01;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(requestReplyTo = @To("${replyTo}"))
@Import(EmbeddedArtemisConfig.class)
public class AppConfigCase01 {
    @ByJms(value = @To("queue"))
    public interface Case01 {
    }
}
