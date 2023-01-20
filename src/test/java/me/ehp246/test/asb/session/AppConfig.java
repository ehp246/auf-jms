package me.ehp246.test.asb.session;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.spi.JacksonConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({ @Inbound(value = @From("auf-jms.session"), scan = OnMsg.class, autoStartup = "false") })
@Import({ JacksonConfig.class })
@SpringBootApplication
class AppConfig {

}
