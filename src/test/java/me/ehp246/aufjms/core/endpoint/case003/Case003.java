package me.ehp246.aufjms.core.endpoint.case003;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;

/**
 * This class should not be scanned because of duplicate named invocations.
 * 
 * @author Lei Yang
 *
 */
@ForJms
public class Case003 {
    @Invoke("m001")
    public void m001() {
    }

    @Invoke("m001")
    public void m001(int i) {
    }

}
