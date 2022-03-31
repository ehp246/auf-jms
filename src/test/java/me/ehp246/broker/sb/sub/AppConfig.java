package me.ehp246.broker.sb.sub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From.Sub;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.util.SimpleServiceBusConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({
        @Inbound(value = @From(value = "auf-jms.echo.event", type = DestinationType.TOPIC, sub = @Sub("sub1"))),
        @Inbound(value = @From(value = "auf-jms.echo.event", type = DestinationType.TOPIC, sub = @Sub("sub1"))) })
@Import({ SimpleServiceBusConfig.class })
@SpringBootApplication
class AppConfig {

}
