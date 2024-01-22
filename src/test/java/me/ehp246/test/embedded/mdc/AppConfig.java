package me.ehp246.test.embedded.mdc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({ @Inbound(value = @From(TestQueueListener.DESTINATION_NAME),
        invocationListener = "msgMDCInvocationLIstener") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
}
