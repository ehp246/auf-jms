package me.ehp246.test.asb.eventgrid.storage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.asb.SimpleServiceBusConfig;

/**
 * @author Lei Yang
 *
 */
@EnableForJms({
        @Inbound(value = @From(value = "blob-inbox"), scan = EventLogger.class) })
@Import({ SimpleServiceBusConfig.class })
@SpringBootApplication
class AppConfig {
}
