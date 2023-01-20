package me.ehp246.test.embedded.enablebyjms.case03;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
class AppConfigs {
    @EnableByJms(ttl = "PT1S")
    @Import({ EmbeddedArtemisConfig.class })
    static class Config01 {
    }

    @EnableByJms
    @Import({ EmbeddedArtemisConfig.class })
    static class Config02 {
    }

    @EnableByJms(ttl = "${ttl}")
    @Import({ EmbeddedArtemisConfig.class })
    static class Config03 {
    }
}
