package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case10;

import me.ehp246.aufjms.api.annotation.ForJmsType;

/**
 * @author Lei Yang
 *
 */
public class Cases10 {
    @ForJmsType("Case${number}")
    public static class Case01 {
        public void invoke() {
        }
    }
}
