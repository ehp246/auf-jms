package me.ehp246.broker.sb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.util.SimpleServiceBusConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@SpringBootApplication
@Import(SimpleServiceBusConfig.class)
class AppConfig {

}
