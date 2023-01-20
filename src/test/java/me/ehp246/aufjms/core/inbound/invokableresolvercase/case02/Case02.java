package me.ehp246.aufjms.core.inbound.invokableresolvercase.case02;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJmsType({ "Case01.*" })
public class Case02 {
    @Invoking
    public void perform() {
    }
}
