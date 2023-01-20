package me.ehp246.aufjms.core.inbound.invokableresolvercase.case07;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
// Duplications on the same class should be NOT accepted.
@ForJmsType({ "Case", "Case" })
class Case07 {
    @Invoking
    public void m() {

    }
}
