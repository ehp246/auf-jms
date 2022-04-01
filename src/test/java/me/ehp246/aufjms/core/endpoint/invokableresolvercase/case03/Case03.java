package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case03;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJmsType({ ".*" })
public class Case03 {
    @Invoking
    public void perform() {
    }
}
