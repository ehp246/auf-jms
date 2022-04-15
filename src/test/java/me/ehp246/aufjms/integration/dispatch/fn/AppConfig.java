package me.ehp246.aufjms.integration.dispatch.fn;

import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.EnableByJms;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms(dispatchFns = "")
class AppConfig {
}
