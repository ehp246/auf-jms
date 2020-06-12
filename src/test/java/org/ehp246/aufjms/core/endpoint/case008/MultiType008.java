package org.ehp246.aufjms.core.endpoint.case008;

import org.ehp246.aufjms.annotation.Executing;
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
@ForMsg({ "", " ", "MultiType008-v1", "MultiType008-v2" })
public class MultiType008 {
	@Executing("m001")
	public void m001() {

	}

	@Executing
	public void m002() {

	}
}
