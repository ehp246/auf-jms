package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case11;

import me.ehp246.aufjms.api.annotation.ForJmsType;

/**
 * @author Lei Yang
 *
 */
public class Cases11 {
    @ForJmsType(value = "Case01", properties = { "JMXGroupId", "1", "JMXGroupSeq", "2" })
    public static class Case01 {
        public void invoke() {
        }
    }
}
