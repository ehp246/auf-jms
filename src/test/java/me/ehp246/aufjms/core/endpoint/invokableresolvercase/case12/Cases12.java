package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case12;

import me.ehp246.aufjms.api.annotation.ForJmsType;

/**
 * @author Lei Yang
 *
 */
public class Cases12 {
    @ForJmsType(value = "Case01", properties = { "JMXGroupId", "${1}" })
    public static class Case01 {
        public void invoke() {
        }
    }
}
