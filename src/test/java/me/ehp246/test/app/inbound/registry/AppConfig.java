package me.ehp246.test.app.inbound.registry;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.mock.action.OnMsg;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableForJms({ @Inbound(value = @From("inbox")) })
    static class Config01 {
    }

    @EnableForJms({ @Inbound(value = @From("inbox"), register = OnMsg.class), @Inbound(value = @From("inbox")) })
    static class Config02 {
    }
}
