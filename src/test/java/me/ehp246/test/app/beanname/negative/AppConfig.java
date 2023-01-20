package me.ehp246.test.app.beanname.negative;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableByJms
    static class Config01 {
        @ByJms(@To(""))
        interface Case01 {
        }
    }

    @ByJms(@To(""))
    interface Case01 {
    }
}
