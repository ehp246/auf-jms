package me.ehp246.aufjms.core.dispatch.requestreplyto.case02;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(requestReplyTo = @To(value = "b82fc4f2-66e6-420b-b9c3-dc960638c24b", type = DestinationType.TOPIC))
@Import(EmbeddedArtemisConfig.class)
public class AppConfigCase02 {
    @ByJms(value = @To("queue"))
    public interface Case01 {
    }
}