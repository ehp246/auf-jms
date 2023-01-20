package me.ehp246.aufjms.core.inbound.invokableresolvercase.case04;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
public interface Case04 {
    @ForJmsType({ "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
            "^(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})$" })
    public class Case01 {
        @Invoking
        public void perform() {
        }
    }

    @ForJmsType({ "^\\d+$" })
    public class Case02 {
        @Invoking
        public void perform() {
        }
    }
}