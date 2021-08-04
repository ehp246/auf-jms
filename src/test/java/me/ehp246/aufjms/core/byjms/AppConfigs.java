package me.ehp246.aufjms.core.byjms;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.integration.enablebyjms.case01.ScanCase01;
import me.ehp246.aufjms.integration.enablebyjms.case01.ScanCase02;

/**
 * @author Lei Yang
 *
 */
class AppConfigs {
    @EnableByJms
    static class Config01 {

    }

    @EnableByJms(scan = { ScanCase01.class })
    static class Config02 {

    }

    @EnableByJms(scan = { ScanCase02.class })
    static class Config03 {

    }

    @EnableByJms(scan = { ScanCase01.class }, replyTo = "From Enabled")
    static class ReplyToConfig01 {

    }
}
