package me.ehp246.test.asb.queue;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.inbound.action.InboundEndpointSwitch;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({
        @Inbound(value = @From("auf-jms.inbox"), name = "auf-jms.inbox", scan = InboundEndpointSwitch.class, autoStartup = "false"),
        @Inbound(value = @From(value = "auf-jms.event", type = DestinationType.TOPIC), name = "auf-jms.event") })
@SpringBootApplication
class AppConfig {
}
