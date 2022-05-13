package me.ehp246.aufjms.core.byjms.delay;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.util.JacksonConfig;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableByJms(delay = "${delay:PT0.112S}")
    @Import(JacksonConfig.class)
    static class DelayConfig01 {
    }

    @EnableByJms()
    @Import(JacksonConfig.class)
    static class DelayConfig02 {
    }
}
