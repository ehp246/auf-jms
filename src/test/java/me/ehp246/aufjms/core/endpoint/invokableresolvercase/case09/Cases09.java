package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case09;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
public class Cases09 {
    @ForJmsType("Case01")
    public static class Case01 {
        public void invoke() {

        }
    }

    @ForJmsType("Case02")
    public static class Case02 {
        public void invoke() {

        }

        @Invoking
        public void perform() {

        }
    }
}
