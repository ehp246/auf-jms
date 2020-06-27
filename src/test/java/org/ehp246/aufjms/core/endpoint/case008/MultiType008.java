package org.ehp246.aufjms.core.endpoint.case008;

import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.ForMsg;

/**
 * @author Lei Yang
 *
 */
/**
 * Blank strings should be discarded.
 * 
 * @author Lei Yang
 *
 */
@ForMsg("MultiType008-v1")
public class MultiType008 {
	@Invoking("m001")
	public void m001() {

	}

	@Invoking
	public void m002() {

	}
}
