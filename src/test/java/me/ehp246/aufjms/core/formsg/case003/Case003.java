package me.ehp246.aufjms.core.formsg.case003;

import me.ehp246.aufjms.api.annotation.ForMsg;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * This class should not be scanned because of duplicate named invocations.
 * 
 * @author Lei Yang
 *
 */
@ForMsg
public class Case003 {
	@Invoking("m001")
	public void m001() {
	}

	@Invoking("m001")
	public void m001(int i) {
	}

}
