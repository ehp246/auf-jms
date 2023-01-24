package me.ehp246.test.asb.sub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From.Sub;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.test.asb.SimpleServiceBusConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({
        @Inbound(value = @From(value = "auf-jms.echo.event", type = DestinationType.TOPIC, sub = @Sub("simple-sub")), autoStartup = "false") })
@Import({ SimpleServiceBusConfig.class })
@SpringBootApplication
class AppConfig {

}
