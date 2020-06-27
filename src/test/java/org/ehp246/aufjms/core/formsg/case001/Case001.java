package org.ehp246.aufjms.core.formsg.case001;

import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForMsg
public class Case001 {

	/**
	 * Single default
	 */
	@Invoking
	public void m001() {
	}

	@Invoking("m001-1")
	public void m001(int i) {
	}

	@Invoking("m002")
	public void m002(int i) {
	}

	/**
	 * Should not be registered
	 */
	public void m002() {

	}
}
