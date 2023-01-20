package me.ehp246.test.embedded.endpoint.listenercontainer;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableForJms({
        @Inbound(value = @From(TestQueueListener.DESTINATION_NAME), autoStartup = "${startup}", name = "startup1"),
        @Inbound(value = @From(TestQueueListener.DESTINATION_NAME), name = "startup2"),
        @Inbound(value = @From(TestQueueListener.DESTINATION_NAME), autoStartup = "false", name = "startup3"),
        @Inbound(value = @From(value = TestQueueListener.DESTINATION_NAME, selector = "JMSPriority = 1"), name = "selector1"),
        @Inbound(value = @From(value = TestQueueListener.DESTINATION_NAME, selector = "${selector}"), name = "selector2") })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
}
