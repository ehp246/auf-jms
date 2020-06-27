package org.ehp246.aufjms.core.formsg.case002;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Invoking;

/**
 * This class should not load because of two default target methods.
 * 
 * @author Lei Yang
 *
 */
@ForMsg
public class Case002 {

	@Invoking
	public void m001() {
	}

	@Invoking
	public void m001(int i) {
	}

}
